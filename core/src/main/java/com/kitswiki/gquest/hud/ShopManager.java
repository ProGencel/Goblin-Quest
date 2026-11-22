package com.kitswiki.gquest.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.kitswiki.gquest.components.InventoryComponent;

public class ShopManager {


    private ObjectMap<String, ShopItem[]> shops;

    public static class ShopItem
    {
        public String id;
        public String name;
        public String price;
    }

    public void readJson(I18NBundle bundle)
    {
        Json json = new Json();
        if(shops!=null)
        {
            shops = null;
        }
        shops = json.fromJson(ObjectMap.class, ShopItem[].class, Gdx.files.internal("data/shops.json"));

        try {
            resolveTexts(bundle);
        }catch (Exception e)
        {
            throw new NullPointerException("check .props");
        }
    }

    public ShopItem[] getItems(String shopId)
    {
        ShopItem[] items = shops.get(shopId);
        if(items == null)
        {
            throw new GdxRuntimeException("Shop cannot find : " + shopId);
        }
        return items;
    }

    public boolean tryBuy(ShopItem item, InventoryComponent inv)
    {
        int itemPrice = Integer.parseInt(item.price);
        if(inv.gold < itemPrice)
        {
            return false;
        }
        inv.gold -= itemPrice;
        inv.inventory.add(item.id);
        return true;
    }

    private void resolveTexts(I18NBundle bundle) {
        for (ShopItem[] items : shops.values()) {
            for (ShopItem item : items) {
                if (item.name != null) {
                    item.name = bundle.get(item.name);
                }
            }
        }
    }

}
