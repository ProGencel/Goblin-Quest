package com.kitswiki.gquest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.kitswiki.gquest.components.InventoryComponent;

public class ShopManager {


    private ObjectMap<String, ShopItem[]> shops;

    public ShopManager() {
        readJson();
    }

    public static class ShopItem
    {
        public String id;
        public String name;
        public String price;
    }

    public void readJson()
    {
        Json json = new Json();
        shops = json.fromJson(ObjectMap.class, ShopItem[].class, Gdx.files.internal("data/shops.json"));
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

}
