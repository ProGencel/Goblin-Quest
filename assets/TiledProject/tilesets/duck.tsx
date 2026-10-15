<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.12.2" name="duck" tilewidth="16" tileheight="16" tilecount="4" columns="4">
 <image source="duck.png" width="64" height="16"/>
 <tile id="0" type="collectable">
  <properties>
   <property name="isAnim" type="bool" value="true"/>
   <property name="spawn_flags" value="duck_can_show,duckGob.duck_collected"/>
  </properties>
  <objectgroup draworder="index" id="2">
   <object id="1" x="3.59375" y="10.625" width="8.34375" height="2.0625"/>
  </objectgroup>
  <animation>
   <frame tileid="0" duration="200"/>
   <frame tileid="1" duration="200"/>
   <frame tileid="2" duration="200"/>
   <frame tileid="3" duration="200"/>
  </animation>
 </tile>
</tileset>
