package com.kitswiki.gquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Vector2;

/**
 * Tiled haritalarından veri okuyan genel amaçlı yardımcı sınıf.
 * Dışarı verdiği tüm konum ve boyutlar DÜNYA BİRİMİNDEDİR (piksel * unitScale).
 * Box2D'yi ve Ashley'yi bilmez.
 */
public class TiledMapReader {

    /** Çarpışma/alan şekli. Tüm koordinatlar dünya biriminde ve mutlaktır. */
    public static class MapShape {
        public enum Type { RECTANGLE, ELLIPSE, POLYGON, POLYLINE }

        public final Type type;
        public final Rectangle bounds;
        public final float[] vertices;
        public final MapProperties properties;

        MapShape(Type type, Rectangle bounds, float[] vertices, MapProperties properties) {
            this.type = type;
            this.bounds = bounds;
            this.vertices = vertices;
            this.properties = properties;
        }

        public boolean has(String key) { return properties.containsKey(key); }
        public String getString(String key, String def) { return TiledMapReader.getString(properties, key, def); }
        public int getInt(String key, int def)          { return TiledMapReader.getInt(properties, key, def); }
        public float getFloat(String key, float def)    { return TiledMapReader.getFloat(properties, key, def); }
        public boolean getBool(String key, boolean def) { return TiledMapReader.getBool(properties, key, def); }
    }

    /**
     * Haritaya konmuş bir Point nesnesi: isim + konum + custom property'ler.
     * Konum dünya biriminde. (Dikdörtgen nesneden üretilirse merkezi alınır.)
     */
    public static class MapPoint {
        public final String name;               // Tiled'daki nesne adı (boş olabilir)
        public final float x, y;                // dünya birimi
        public final MapProperties properties;  // nesnenin custom property'leri

        MapPoint(String name, float x, float y, MapProperties properties) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.properties = properties;
        }

        /** Her çağrıda yeni Vector2 üretir. */
        public Vector2 getPosition() { return new Vector2(x, y); }

        /** Tiled'daki "Class/Type" alanı (yoksa null). */
        public String getType() {
            return TiledMapReader.getString(properties, "type",
                TiledMapReader.getString(properties, "class", null));
        }

        public boolean has(String key) { return properties.containsKey(key); }
        public String getString(String key, String def) { return TiledMapReader.getString(properties, key, def); }
        public int getInt(String key, int def)          { return TiledMapReader.getInt(properties, key, def); }
        public float getFloat(String key, float def)    { return TiledMapReader.getFloat(properties, key, def); }
        public boolean getBool(String key, boolean def) { return TiledMapReader.getBool(properties, key, def); }
    }

    public static class MapTileObject {
        public final String name;
        public final TextureRegion region;
        public final float x, y;          // sol alt köşe, dünya birimi
        public final float width, height; // dünya birimi
        public final MapProperties properties;      // sadece nesnenin kendi property'leri
        public final MapProperties tileProperties;  // tileset'te tile'a eklenenler
        public final MapProperties allProperties;   // birleşik (nesne, tile'ı ezer)
        public final Array<MapShape> shapes;

        MapTileObject(String name, TextureRegion region, float x, float y, float w, float h,
                      MapProperties objProps, MapProperties tileProps, Array<MapShape> shapes) {
            this.name = name;
            this.region = region; this.x = x; this.y = y;
            this.width = w; this.height = h;
            this.properties = objProps;
            this.tileProperties = tileProps;
            this.shapes = shapes;
            this.allProperties = new MapProperties();
            this.allProperties.putAll(tileProps);
            this.allProperties.putAll(objProps);
        }

        public boolean has(String key) { return allProperties.containsKey(key); }
        public String getString(String key, String def) { return TiledMapReader.getString(allProperties, key, def); }
        public int getInt(String key, int def)          { return TiledMapReader.getInt(allProperties, key, def); }
        public float getFloat(String key, float def)    { return TiledMapReader.getFloat(allProperties, key, def); }
        public boolean getBool(String key, boolean def) { return TiledMapReader.getBool(allProperties, key, def); }
    }

    private final TiledMap map;
    private final float scale;
    private final int tileWidthPx;
    private final int tileHeightPx;
    private final int widthInTiles;
    private final int heightInTiles;

    public TiledMapReader(TiledMap map, float unitScale) {
        this.map = map;
        this.scale = unitScale;
        MapProperties p = map.getProperties();
        this.tileWidthPx = p.get("tilewidth", Integer.class);
        this.tileHeightPx = p.get("tileheight", Integer.class);
        this.widthInTiles = p.get("width", Integer.class);
        this.heightInTiles = p.get("height", Integer.class);
    }

    // ------------------------------------------------------------------
    // HARİTA BİLGİSİ
    // ------------------------------------------------------------------

    public MapProperties getMapProperties() { return map.getProperties(); }

    public int getWidthInTiles()  { return widthInTiles; }
    public int getHeightInTiles() { return heightInTiles; }

    public float getTileWidthWorld()  { return tileWidthPx * scale; }
    public float getTileHeightWorld() { return tileHeightPx * scale; }

    /** Haritanın dünya birimindeki toplam genişliği (kamera sınırlamak için). */
    public float getWidthWorld()  { return widthInTiles * tileWidthPx * scale; }
    public float getHeightWorld() { return heightInTiles * tileHeightPx * scale; }

    public int worldToTileX(float worldX) { return (int) (worldX / (tileWidthPx * scale)); }
    public int worldToTileY(float worldY) { return (int) (worldY / (tileHeightPx * scale)); }

    // ------------------------------------------------------------------
    // KATMANLAR
    // ------------------------------------------------------------------

    public boolean hasLayer(String name) {
        return map.getLayers().get(name) != null;
    }

    public MapLayer getLayer(String name) {
        MapLayer layer = map.getLayers().get(name);
        if (layer == null) throw new IllegalStateException("Katman yok: " + name);
        return layer;
    }

    public TiledMapTileLayer getTileLayer(String name) {
        MapLayer layer = getLayer(name);
        if (!(layer instanceof TiledMapTileLayer)) {
            throw new IllegalStateException("Bu bir tile katmanı değil: " + name);
        }
        return (TiledMapTileLayer) layer;
    }

    /** Render için katman sırası (mapRenderer.render(int[]) ile kullanılır). */
    public int getLayerIndex(String name) {
        int index = map.getLayers().getIndex(name);
        if (index < 0) throw new IllegalStateException("Katman yok: " + name);
        return index;
    }

    // ------------------------------------------------------------------
    // NESNE KATMANLARI: ham nesne erişimi
    // ------------------------------------------------------------------

    /** Object layer'a yerleştirilmiş tile'ların (masa, sandık vb.) hitbox şekilleri. */
    public Array<MapShape> getTileObjectShapes(String objectLayerName) {
        Array<MapShape> result = new Array<>();
        for (MapTileObject o : getTileObjects(objectLayerName)) result.addAll(o.shapes);
        return result;
    }

    public Array<MapTileObject> getTileObjects(String objectLayerName) {
        Array<MapTileObject> result = new Array<>();
        for (MapObject obj : getLayer(objectLayerName).getObjects()) {
            if (!(obj instanceof TiledMapTileMapObject)) continue;
            result.add(toTileObject((TiledMapTileMapObject) obj));
        }
        return result;
    }

    /** İsmine göre tek bir tile nesnesi. */
    public MapTileObject getTileObject(String objectLayerName, String objectName) {
        MapObject obj = getObject(objectLayerName, objectName);
        if (!(obj instanceof TiledMapTileMapObject)) {
            throw new IllegalStateException("Nesne tile nesnesi değil: " + objectName);
        }
        return toTileObject((TiledMapTileMapObject) obj);
    }

    /** Belirli bir property'si olan tile nesneleri (değeri ne olursa olsun). */
    public Array<MapTileObject> getTileObjectsByProperty(String objectLayerName, String key) {
        Array<MapTileObject> result = new Array<>();
        for (MapTileObject o : getTileObjects(objectLayerName)) {
            if (o.has(key)) result.add(o);
        }
        return result;
    }

    /** Property'si verilen değere eşit olan tile nesneleri (örn. "type" = "chest"). */
    public Array<MapTileObject> getTileObjectsByProperty(String objectLayerName, String key, String value) {
        Array<MapTileObject> result = new Array<>();
        for (MapTileObject o : getTileObjects(objectLayerName)) {
            if (value.equals(o.getString(key, null))) result.add(o);
        }
        return result;
    }

    /** Property'si (key = value) olan İLK tile nesnesi. Bulunamazsa hata fırlatır. */
    public MapTileObject getTileObjectByProperty(String objectLayerName, String key, String value) {
        Array<MapTileObject> found = getTileObjectsByProperty(objectLayerName, key, value);
        if (found.size == 0) {
            throw new IllegalStateException("Tile nesnesi yok: " + key + "=" + value + " (katman: " + objectLayerName + ")");
        }
        return found.first();
    }

    /** Tek bir tile nesnesinin birleşik property'leri (nesne + tile). */
    public static MapProperties getTileObjectProperties(MapObject obj) {
        MapProperties merged = new MapProperties();
        if (obj instanceof TiledMapTileMapObject) {
            merged.putAll(((TiledMapTileMapObject) obj).getTile().getProperties());
        }
        merged.putAll(obj.getProperties());
        return merged;
    }

    private MapTileObject toTileObject(TiledMapTileMapObject t) {
        TextureRegion r = t.getTile().getTextureRegion();

        Array<MapShape> shapes = new Array<>();
        for (MapObject local : t.getTile().getObjects()) {
            MapShape s = toShape(local, 0f, 0f);
            if (s == null) continue;

            MapProperties merged = new MapProperties();
            merged.putAll(t.getTile().getProperties());
            merged.putAll(t.getProperties());
            merged.putAll(local.getProperties());

            shapes.add(placeShape(new MapShape(s.type, s.bounds, s.vertices, merged), t));
        }

        return new MapTileObject(t.getName(), r,
            t.getX() * scale, t.getY() * scale,
            r.getRegionWidth() * scale, r.getRegionHeight() * scale,
            t.getProperties(), t.getTile().getProperties(), shapes);
    }

    /** Tile-yerel şekli; nesnenin konum/flip/scale/rotation değerlerine göre haritaya yerleştirir. */
    private MapShape placeShape(MapShape s, TiledMapTileMapObject t) {
        TextureRegion r = t.getTile().getTextureRegion();
        float tw = r.getRegionWidth() * scale;
        float th = r.getRegionHeight() * scale;
        float ox = t.getX() * scale, oy = t.getY() * scale;

        // Tiled'da rotation saat yönündedir; matematik için eksiye çeviriyoruz.
        float rad = -t.getRotation() * MathUtils.degreesToRadians;
        float cos = MathUtils.cos(rad), sin = MathUtils.sin(rad);

        float[] v;
        if (s.type == MapShape.Type.POLYGON || s.type == MapShape.Type.POLYLINE) {
            v = s.vertices.clone();
        } else { // RECTANGLE / ELLIPSE -> 4 köşe
            Rectangle b = s.bounds;
            v = new float[] {
                b.x, b.y,  b.x + b.width, b.y,
                b.x + b.width, b.y + b.height,  b.x, b.y + b.height };
        }

        for (int i = 0; i < v.length; i += 2) {
            float px = v[i], py = v[i + 1];
            if (t.isFlipHorizontally()) px = tw - px;
            if (t.isFlipVertically())   py = th - py;
            px *= t.getScaleX();
            py *= t.getScaleY();
            v[i]     = ox + px * cos - py * sin;   // pivot: nesnenin sol alt köşesi (Tiled gibi)
            v[i + 1] = oy + px * sin + py * cos;
        }

        boolean rotated = t.getRotation() != 0f;
        switch (s.type) {
            case POLYGON:
            case POLYLINE:
                return new MapShape(s.type, boundsOf(v), v, s.properties);
            case ELLIPSE:   // döndürülmüş elips AABB'ye yaklaştırılır
                return new MapShape(MapShape.Type.ELLIPSE, boundsOf(v), null, s.properties);
            default:        // RECTANGLE
                return rotated
                    ? new MapShape(MapShape.Type.POLYGON, boundsOf(v), v, s.properties)
                    : new MapShape(MapShape.Type.RECTANGLE, boundsOf(v), null, s.properties);
        }
    }

    public MapObject getObject(String layerName, String objectName) {
        MapObject obj = getLayer(layerName).getObjects().get(objectName);
        if (obj == null) {
            throw new IllegalStateException("Nesne yok: " + objectName + " (katman: " + layerName + ")");
        }
        return obj;
    }

    public Array<MapObject> getObjects(String layerName) {
        Array<MapObject> result = new Array<>();
        for (MapObject obj : getLayer(layerName).getObjects()) result.add(obj);
        return result;
    }

    /** Tiled'daki "Class/Type" alanına göre filtreler (örn. "npc", "door"). */
    public Array<MapObject> getObjectsByType(String layerName, String type) {
        Array<MapObject> result = new Array<>();
        for (MapObject obj : getLayer(layerName).getObjects()) {
            MapProperties p = obj.getProperties();
            String t = getString(p, "type", getString(p, "class", null));
            if (type.equals(t)) result.add(obj);
        }
        return result;
    }

    // ------------------------------------------------------------------
    // ÖZELLİĞE (CUSTOM PROPERTY) GÖRE ARAMA
    // name'e ek olarak herhangi bir property ile nesne bulmak için.
    // Değer karşılaştırması string üzerinden yapılır (true -> "true", 5 -> "5").
    // Tile nesnelerinde tileset'teki property'ler de aranır (nesne değeri öncelikli).
    // ------------------------------------------------------------------

    /** Katmanda belirli bir property'si olan nesneler (değeri ne olursa olsun). */
    public Array<MapObject> getObjectsByProperty(String layerName, String key) {
        return filterObjects(layerName, key, null);
    }

    /** Katmanda property'si verilen değere eşit olan nesneler (örn. "spawnId" = "door_1"). */
    public Array<MapObject> getObjectsByProperty(String layerName, String key, String value) {
        return filterObjects(layerName, key, value);
    }

    /** Property'si (key = value) olan İLK nesne. Bulunamazsa hata fırlatır. */
    public MapObject getObjectByProperty(String layerName, String key, String value) {
        Array<MapObject> found = filterObjects(layerName, key, value);
        if (found.size == 0) {
            throw new IllegalStateException("Nesne yok: " + key + "=" + value + " (katman: " + layerName + ")");
        }
        return found.first();
    }

    /** Property'ye göre şekiller (dikdörtgen, elips, poligon, çizgi). Point'ler atlanır. */
    public Array<MapShape> getShapesByProperty(String layerName, String key) {
        return shapesOf(filterObjects(layerName, key, null));
    }

    public Array<MapShape> getShapesByProperty(String layerName, String key, String value) {
        return shapesOf(filterObjects(layerName, key, value));
    }

    public MapShape getShapeByProperty(String layerName, String key, String value) {
        Array<MapShape> found = getShapesByProperty(layerName, key, value);
        if (found.size == 0) {
            throw new IllegalStateException("Şekil yok: " + key + "=" + value + " (katman: " + layerName + ")");
        }
        return found.first();
    }

    /** Property'ye göre dikdörtgenler. */
    public Array<Rectangle> getRectsByProperty(String layerName, String key) {
        return rectsOf(filterObjects(layerName, key, null));
    }

    public Array<Rectangle> getRectsByProperty(String layerName, String key, String value) {
        return rectsOf(filterObjects(layerName, key, value));
    }

    public Rectangle getRectByProperty(String layerName, String key, String value) {
        Array<Rectangle> found = getRectsByProperty(layerName, key, value);
        if (found.size == 0) {
            throw new IllegalStateException("Dikdörtgen yok: " + key + "=" + value + " (katman: " + layerName + ")");
        }
        return found.first();
    }

    // ------------------------------------------------------------------
    // POINT (doğma noktaları, waypoint'ler)
    // ------------------------------------------------------------------

    /**
     * İsmine göre tek nokta. Nesne Point ise konumu, dikdörtgen ise merkezi alınır.
     * Konum için: point.x / point.y veya point.getPosition().
     */
    public MapPoint getPoint(String layerName, String objectName) {
        return toMapPoint(getObject(layerName, objectName));
    }

    /** Katmandaki TÜM Point nesneleri (isim + konum + property'leriyle). */
    public Array<MapPoint> getPoints(String layerName) {
        return pointsOf(getLayer(layerName).getObjects());
    }

    /** Belirli bir property'si olan Point'ler (değeri ne olursa olsun). */
    public Array<MapPoint> getPointsByProperty(String layerName, String key) {
        return pointsOf(filterObjects(layerName, key, null));
    }

    /** Property'si verilen değere eşit olan Point'ler (örn. "type" = "spawn"). */
    public Array<MapPoint> getPointsByProperty(String layerName, String key, String value) {
        return pointsOf(filterObjects(layerName, key, value));
    }

    /** Property'si (key = value) olan İLK nokta. Bulunamazsa hata fırlatır. */
    public MapPoint getPointByProperty(String layerName, String key, String value) {
        return toMapPoint(getObjectByProperty(layerName, key, value));
    }

    /** Tiled'daki "Class/Type" alanına göre Point'ler (örn. "spawn", "waypoint"). */
    public Array<MapPoint> getPointsByType(String layerName, String type) {
        return pointsOf(getObjectsByType(layerName, type));
    }

    /** Point ise konumu, dikdörtgen ise merkezini verir. */
    public Vector2 toPoint(MapObject obj) {
        if (obj instanceof PointMapObject) {
            Vector2 p = ((PointMapObject) obj).getPoint();
            return new Vector2(p.x * scale, p.y * scale);
        }
        if (obj instanceof RectangleMapObject) {
            Rectangle r = getBounds(obj);
            return new Vector2(r.x + r.width / 2f, r.y + r.height / 2f);
        }
        throw new IllegalStateException("Nesne point/dikdörtgen değil: " + obj.getName());
    }

    /** Point/dikdörtgen nesneyi isim + konum + property içeren MapPoint'e çevirir. */
    public MapPoint toMapPoint(MapObject obj) {
        Vector2 pos = toPoint(obj);
        return new MapPoint(obj.getName(), pos.x, pos.y, obj.getProperties());
    }

    // ------------------------------------------------------------------
    // DİKDÖRTGEN, ELİPS, POLİGON, ÇİZGİ
    // ------------------------------------------------------------------

    public Rectangle getRect(String layerName, String objectName) {
        return getBounds(getObject(layerName, objectName));
    }

    /** Katmandaki tüm dikdörtgenler. */
    public Array<Rectangle> getRects(String layerName) {
        Array<Rectangle> result = new Array<>();
        for (MapObject obj : getLayer(layerName).getObjects()) {
            if (obj instanceof RectangleMapObject) result.add(getBounds(obj));
        }
        return result;
    }

    /** Katmandaki tüm şekiller (dikdörtgen, elips, poligon, çizgi). Point'ler atlanır. */
    public Array<MapShape> getShapes(String layerName) {
        Array<MapShape> result = new Array<>();
        for (MapObject obj : getLayer(layerName).getObjects()) {
            MapShape s = toShape(obj, 0f, 0f);
            if (s != null) result.add(s);
        }
        return result;
    }

    public MapShape getShape(String layerName, String objectName) {
        MapShape s = toShape(getObject(layerName, objectName), 0f, 0f);
        if (s == null) throw new IllegalStateException("Nesne şekil değil: " + objectName);
        return s;
    }

    /** Herhangi bir nesnenin sınırlayıcı kutusu (dünya biriminde, yeni kopya). */
    public Rectangle getBounds(MapObject obj) {
        if (obj instanceof TiledMapTileMapObject) {            // katmana yerleştirilmiş tile
            TiledMapTileMapObject t = (TiledMapTileMapObject) obj;
            float w = getFloat(t.getProperties(), "width", t.getTile().getTextureRegion().getRegionWidth());
            float h = getFloat(t.getProperties(), "height", t.getTile().getTextureRegion().getRegionHeight());
            return new Rectangle(t.getX() * scale, t.getY() * scale, w * scale, h * scale);
        }
        MapShape s = toShape(obj, 0f, 0f);
        if (s != null) return s.bounds;
        if (obj instanceof PointMapObject) {
            Vector2 p = toPoint(obj);
            return new Rectangle(p.x, p.y, 0f, 0f);
        }
        throw new IllegalStateException("Desteklenmeyen nesne türü: " + obj.getName());
    }

    // ------------------------------------------------------------------
    // TILESET ÇARPIŞMALARI
    // ------------------------------------------------------------------

    /**
     * Tile Collision Editor ile tileset'te çizilen şekilleri,
     * tile'ın haritadaki konumuna taşıyarak döndürür.
     */
    public Array<MapShape> getTileCollisionShapes(String tileLayerName) {
        Array<MapShape> result = new Array<>();
        collectTileShapes(getTileLayer(tileLayerName), result);
        return result;
    }

    /** Tüm tile katmanlarını tarar. */
    public Array<MapShape> getTileCollisionShapes() {
        Array<MapShape> result = new Array<>();
        for (TiledMapTileLayer layer : map.getLayers().getByType(TiledMapTileLayer.class)) {
            collectTileShapes(layer, result);
        }
        return result;
    }

    private void collectTileShapes(TiledMapTileLayer layer, Array<MapShape> out) {
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell == null) continue;
                MapObjects objects = cell.getTile().getObjects();
                if (objects.getCount() == 0) continue;

                float offsetX = x * layer.getTileWidth();
                float offsetY = y * layer.getTileHeight();
                for (MapObject obj : objects) {
                    MapShape s = toShape(obj, offsetX, offsetY);
                    if (s != null) out.add(s);
                }
            }
        }
    }

    /**
     * Tileset'te tile'a eklenen bir boolean property'ye göre (örn. "solid" = true)
     * tile'ların tam kare kutularını döndürür.
     * merge = true ise yan yana tile'lar tek uzun dikdörtgene birleşir
     * (daha az Box2D body, birleşim yerlerinde takılma olmaz).
     */
    public Array<Rectangle> getTileRectsByProperty(String tileLayerName, String key, boolean merge) {
        TiledMapTileLayer layer = getTileLayer(tileLayerName);
        float tw = layer.getTileWidth() * scale;
        float th = layer.getTileHeight() * scale;
        Array<Rectangle> result = new Array<>();

        for (int y = 0; y < layer.getHeight(); y++) {
            int runStart = -1;
            for (int x = 0; x <= layer.getWidth(); x++) {
                boolean solid = x < layer.getWidth() && cellHasTrue(layer.getCell(x, y), key);
                if (solid) {
                    if (runStart < 0) runStart = x;
                    if (!merge) {
                        result.add(new Rectangle(x * tw, y * th, tw, th));
                        runStart = -1;
                    }
                } else if (runStart >= 0) {
                    result.add(new Rectangle(runStart * tw, y * th, (x - runStart) * tw, th));
                    runStart = -1;
                }
            }
        }
        return result;
    }

    // ------------------------------------------------------------------
    // TILE ÖZELLİKLERİ (oyun sırasında sorgulamak için)
    // ------------------------------------------------------------------

    /** O hücredeki tile'ın property'leri. Hücre boşsa null. */
    public MapProperties getTileProperties(String tileLayerName, int tileX, int tileY) {
        TiledMapTileLayer.Cell cell = getTileLayer(tileLayerName).getCell(tileX, tileY);
        return cell == null ? null : cell.getTile().getProperties();
    }

    /** Dünya konumundaki tile'ın string property'si (örn. zemin türü "grass"). */
    public String getTileStringAt(String tileLayerName, float worldX, float worldY, String key, String def) {
        MapProperties p = getTileProperties(tileLayerName, worldToTileX(worldX), worldToTileY(worldY));
        return p == null ? def : getString(p, key, def);
    }

    // ------------------------------------------------------------------
    // PROPERTY OKUMA (Tiled'daki custom property'ler için)
    // ------------------------------------------------------------------

    public static boolean has(MapObject obj, String key) { return obj.getProperties().containsKey(key); }

    public static String getString(MapObject obj, String key, String def) { return getString(obj.getProperties(), key, def); }
    public static int getInt(MapObject obj, String key, int def)          { return getInt(obj.getProperties(), key, def); }
    public static float getFloat(MapObject obj, String key, float def)    { return getFloat(obj.getProperties(), key, def); }
    public static boolean getBool(MapObject obj, String key, boolean def) { return getBool(obj.getProperties(), key, def); }

    public static String getString(MapProperties p, String key, String def) {
        Object v = p.get(key);
        return v == null ? def : v.toString();
    }

    public static int getInt(MapProperties p, String key, int def) {
        Object v = p.get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        if (v instanceof String) { try { return Integer.parseInt((String) v); } catch (NumberFormatException ignored) {} }
        return def;
    }

    public static float getFloat(MapProperties p, String key, float def) {
        Object v = p.get(key);
        if (v instanceof Number) return ((Number) v).floatValue();
        if (v instanceof String) { try { return Float.parseFloat((String) v); } catch (NumberFormatException ignored) {} }
        return def;
    }

    public static boolean getBool(MapProperties p, String key, boolean def) {
        Object v = p.get(key);
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof String) return Boolean.parseBoolean((String) v);
        return def;
    }

    // ------------------------------------------------------------------
    // İÇ YARDIMCILAR
    // ------------------------------------------------------------------

    private boolean cellHasTrue(TiledMapTileLayer.Cell cell, String key) {
        if (cell == null) return false;
        TiledMapTile tile = cell.getTile();
        return tile != null && getBool(tile.getProperties(), key, false);
    }

    /** value == null ise sadece property'nin varlığına bakar. */
    private static boolean propertyMatches(MapProperties p, String key, String value) {
        if (!p.containsKey(key)) return false;
        return value == null || value.equals(getString(p, key, null));
    }

    private Array<MapObject> filterObjects(String layerName, String key, String value) {
        Array<MapObject> result = new Array<>();
        for (MapObject obj : getLayer(layerName).getObjects()) {
            if (propertyMatches(getTileObjectProperties(obj), key, value)) result.add(obj);
        }
        return result;
    }

    private Array<MapPoint> pointsOf(Iterable<MapObject> objects) {
        Array<MapPoint> result = new Array<>();
        for (MapObject obj : objects) {
            if (obj instanceof PointMapObject) result.add(toMapPoint(obj));
        }
        return result;
    }

    private Array<MapShape> shapesOf(Array<MapObject> objects) {
        Array<MapShape> result = new Array<>();
        for (MapObject obj : objects) {
            MapShape s = toShape(obj, 0f, 0f);
            if (s != null) result.add(s);
        }
        return result;
    }

    private Array<Rectangle> rectsOf(Array<MapObject> objects) {
        Array<Rectangle> result = new Array<>();
        for (MapObject obj : objects) {
            if (obj instanceof RectangleMapObject) result.add(getBounds(obj));
        }
        return result;
    }

    /** offX/offY: piksel cinsinden kaydırma (tile içi şekilleri haritaya taşımak için). */
    private MapShape toShape(MapObject obj, float offX, float offY) {
        MapProperties props = obj.getProperties();

        if (obj instanceof RectangleMapObject) {
            Rectangle r = ((RectangleMapObject) obj).getRectangle();
            return new MapShape(MapShape.Type.RECTANGLE, scaledRect(r.x + offX, r.y + offY, r.width, r.height), null, props);
        }
        if (obj instanceof EllipseMapObject) {
            Ellipse e = ((EllipseMapObject) obj).getEllipse();
            return new MapShape(MapShape.Type.ELLIPSE, scaledRect(e.x + offX, e.y + offY, e.width, e.height), null, props);
        }
        if (obj instanceof PolygonMapObject) {
            float[] v = scaledVertices(((PolygonMapObject) obj).getPolygon().getTransformedVertices(), offX, offY);
            return new MapShape(MapShape.Type.POLYGON, boundsOf(v), v, props);
        }
        if (obj instanceof PolylineMapObject) {
            float[] v = scaledVertices(((PolylineMapObject) obj).getPolyline().getTransformedVertices(), offX, offY);
            return new MapShape(MapShape.Type.POLYLINE, boundsOf(v), v, props);
        }
        return null;
    }

    private Rectangle scaledRect(float x, float y, float w, float h) {
        return new Rectangle(x * scale, y * scale, w * scale, h * scale);
    }

    private float[] scaledVertices(float[] v, float offX, float offY) {
        float[] out = new float[v.length];
        for (int i = 0; i < v.length; i += 2) {
            out[i] = (v[i] + offX) * scale;
            out[i + 1] = (v[i + 1] + offY) * scale;
        }
        return out;
    }

    private Rectangle boundsOf(float[] v) {
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (int i = 0; i < v.length; i += 2) {
            minX = Math.min(minX, v[i]);     maxX = Math.max(maxX, v[i]);
            minY = Math.min(minY, v[i + 1]); maxY = Math.max(maxY, v[i + 1]);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
}
