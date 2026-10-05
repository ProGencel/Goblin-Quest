<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.12.2" name="props" tilewidth="112" tileheight="112" tilecount="3" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="48"/>
   <property name="OFFSET_Y" type="float" value="9"/>
  </properties>
  <image source="tail.png" width="32" height="48"/>
  <objectgroup draworder="index" id="4">
   <object id="3" x="7.0449" y="29.0129" width="17.9531" height="5.90862"/>
   <object id="4" x="12.1203" y="18.9" width="7.84029" height="3.86333"/>
   <object id="5" x="7.61304" y="23.1421" width="4.20421" height="5.87075"/>
   <object id="6" x="20.1499" y="22.4982" width="4.27997" height="5.83287"/>
  </objectgroup>
 </tile>
 <tile id="2" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="112"/>
   <property name="OFFSET_Y" type="float" value="16"/>
  </properties>
  <image source="house.png" width="112" height="112"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="10.2727" y="44" width="93.4545" height="51.9091">
    <properties>
     <property name="FRAME_H" type="int" value="1"/>
    </properties>
   </object>
  </objectgroup>
 </tile>
 <tile id="4" type="goblin">
  <properties>
   <property name="FRAME_H" type="int" value="64"/>
   <property name="OFFSET_Y" type="float" value="25"/>
   <property name="isFlipped" type="bool" value="true"/>
  </properties>
  <image source="goblin_icon.png" width="18" height="16"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="5.04444" y="10.9778" width="6.86667" height="3.88889"/>
  </objectgroup>
 </tile>
</tileset>
