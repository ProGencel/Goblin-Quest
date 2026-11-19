<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.12.2" name="props" tilewidth="288" tileheight="192" tilecount="43" columns="0">
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
   <property name="identity" value="def"/>
   <property name="isFlipped" type="bool" value="true"/>
  </properties>
  <image source="goblin_icon.png" width="18" height="16"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="5.04444" y="10.9778" width="6.86667" height="3.88889"/>
  </objectgroup>
 </tile>
 <tile id="5" type="bad_goblin">
  <properties>
   <property name="FRAME_H" type="int" value="64"/>
   <property name="OFFSET_Y" type="float" value="25"/>
  </properties>
  <image source="bad_goblin_icon.png" width="18" height="16"/>
  <objectgroup draworder="index" id="2">
   <object id="2" x="6.62826" y="10.8704" width="0.151503" height="0.0378758">
    <ellipse/>
   </object>
   <object id="3" x="6.59039" y="10.8325" width="4.5451" height="3.97696">
    <ellipse/>
   </object>
   <object id="4" x="11.1355" y="14.8094" width="0.151503" height="0.0378758">
    <ellipse/>
   </object>
  </objectgroup>
 </tile>
 <tile id="6" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="48"/>
   <property name="OFFSET_Y" type="float" value="8"/>
  </properties>
  <image source="empty_statue.png" width="32" height="48"/>
  <objectgroup draworder="index" id="6">
   <object id="5" x="1.95652" y="26.2609" width="28.087" height="15.6087"/>
  </objectgroup>
 </tile>
 <tile id="7" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="32"/>
   <property name="OFFSET_Y" type="float" value="7"/>
  </properties>
  <image source="coal.png" width="32" height="32"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="10.1304" y="18.5217" width="11.7826" height="6.21739"/>
  </objectgroup>
 </tile>
 <tile id="9" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="32"/>
   <property name="OFFSET_Y" type="float" value="8"/>
  </properties>
  <image source="bush_big.png" width="32" height="32"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="5.94158" y="14.1875" width="19.962" height="9.28125"/>
  </objectgroup>
 </tile>
 <tile id="10" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="32"/>
   <property name="OFFSET_Y" type="float" value="8"/>
  </properties>
  <image source="bush_green_fruit.png" width="32" height="32"/>
  <objectgroup draworder="index" id="2">
   <object id="3" x="6.06248" y="14.2289" width="19.962" height="9.28125"/>
  </objectgroup>
 </tile>
 <tile id="11" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="32"/>
   <property name="OFFSET_Y" type="float" value="8"/>
  </properties>
  <image source="bush_red_fruit.png" width="32" height="32"/>
  <objectgroup draworder="index" id="4">
   <object id="4" x="6.10596" y="14.1855" width="19.962" height="9.28125"/>
  </objectgroup>
 </tile>
 <tile id="12" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="64"/>
  </properties>
  <image source="house_green.png" width="80" height="64"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="11.125" y="40.625" width="58.125" height="22.5"/>
  </objectgroup>
 </tile>
 <tile id="13" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="48"/>
   <property name="OFFSET_Y" type="float" value="11"/>
  </properties>
  <image source="tree1.png" width="32" height="48"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="12.1304" y="31.0435" width="7.78261" height="6.26087"/>
  </objectgroup>
 </tile>
 <tile id="14" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="48"/>
   <property name="OFFSET_Y" type="float" value="2"/>
  </properties>
  <image source="tree2.png" width="16" height="48"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="5.3125" y="40.25" width="5.53125" height="5.875"/>
  </objectgroup>
 </tile>
 <tile id="15" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="48"/>
   <property name="OFFSET_Y" type="float" value="17"/>
  </properties>
  <image source="tree3.png" width="32" height="48"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="6.95245" y="22.6563" width="17.9565" height="8.15625"/>
  </objectgroup>
 </tile>
 <tile id="16" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="96"/>
  </properties>
  <image source="house_red.png" width="80" height="96"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="10.875" y="46.625" width="59.25" height="48.75"/>
  </objectgroup>
 </tile>
 <tile id="17" type="collectable">
  <properties>
   <property name="FRAME_H" type="int" value="16"/>
   <property name="OFFSET_Y" type="float" value="3"/>
   <property name="cid" value="duck"/>
  </properties>
  <image source="duck_tired.png" width="16" height="16"/>
  <objectgroup draworder="index" id="2">
   <object id="1" type="collectable" x="4.06667" y="10.1111" width="7.97778" height="1.88889"/>
  </objectgroup>
 </tile>
 <tile id="18" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="48"/>
   <property name="OFFSET_Y" type="float" value="12"/>
  </properties>
  <image source="stand.png" width="96" height="48"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="4.25" y="28.125" width="86.375" height="7.375"/>
  </objectgroup>
 </tile>
 <tile id="20" type="static">
  <image source="bigHouseRoof.png" width="288" height="192"/>
 </tile>
 <tile id="23" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="22"/>
  </properties>
  <image source="BigHouse/wall2.png" width="53" height="22"/>
 </tile>
 <tile id="24" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="22"/>
  </properties>
  <image source="BigHouse/wall3.png" width="48" height="22"/>
 </tile>
 <tile id="25">
  <image source="bigHouseInterior.png" width="288" height="192"/>
 </tile>
 <tile id="26" type="skeleton">
  <image source="skeleton.png" width="13" height="16"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="4.08696" y="10.4783" width="6.04348" height="5.82609">
    <ellipse/>
   </object>
  </objectgroup>
 </tile>
 <tile id="27" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="22"/>
  </properties>
  <image source="BigHouse/wall1.png" width="144" height="22"/>
 </tile>
 <tile id="29" type="static">
  <properties>
   <property name="FRAME_H" type="int" value="33"/>
  </properties>
  <image source="lantern.png" width="11" height="33"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="3.91111" y="30.8667" width="3.33333" height="2.17778"/>
  </objectgroup>
 </tile>
 <tile id="32" type="static">
  <properties>
   <property name="isAnim" type="bool" value="true"/>
  </properties>
  <image source="cavelantern/lanterncave_0.png" width="16" height="54"/>
  <objectgroup draworder="index" id="2">
   <object id="1" type="static" x="5.1875" y="51" width="4.5625" height="2.96875">
    <properties>
     <property name="isAnim" type="bool" value="true"/>
    </properties>
   </object>
  </objectgroup>
  <animation>
   <frame tileid="32" duration="110"/>
   <frame tileid="33" duration="110"/>
   <frame tileid="34" duration="110"/>
   <frame tileid="35" duration="110"/>
   <frame tileid="36" duration="110"/>
   <frame tileid="37" duration="110"/>
   <frame tileid="38" duration="110"/>
   <frame tileid="39" duration="110"/>
   <frame tileid="40" duration="110"/>
   <frame tileid="41" duration="110"/>
  </animation>
 </tile>
 <tile id="33">
  <image source="cavelantern/lanterncave_1.png" width="16" height="54"/>
 </tile>
 <tile id="34">
  <image source="cavelantern/lanterncave_2.png" width="16" height="54"/>
 </tile>
 <tile id="35">
  <image source="cavelantern/lanterncave_3.png" width="16" height="54"/>
 </tile>
 <tile id="36">
  <image source="cavelantern/lanterncave_4.png" width="16" height="54"/>
 </tile>
 <tile id="37">
  <image source="cavelantern/lanterncave_5.png" width="16" height="54"/>
 </tile>
 <tile id="38">
  <image source="cavelantern/lanterncave_6.png" width="16" height="54"/>
 </tile>
 <tile id="39">
  <image source="cavelantern/lanterncave_7.png" width="16" height="54"/>
 </tile>
 <tile id="40">
  <image source="cavelantern/lanterncave_8.png" width="16" height="54"/>
 </tile>
 <tile id="41">
  <image source="cavelantern/lanterncave_9.png" width="16" height="54"/>
 </tile>
 <tile id="43" type="static">
  <properties>
   <property name="isAnim" type="bool" value="true"/>
  </properties>
  <image source="cavelantern/Campfire_0.png" width="25" height="32"/>
  <objectgroup draworder="index" id="2">
   <object id="1" x="5.5" y="22.0938" width="15.875" height="8.28125"/>
  </objectgroup>
  <animation>
   <frame tileid="43" duration="110"/>
   <frame tileid="44" duration="110"/>
   <frame tileid="46" duration="110"/>
   <frame tileid="47" duration="110"/>
   <frame tileid="48" duration="110"/>
   <frame tileid="49" duration="110"/>
   <frame tileid="50" duration="110"/>
   <frame tileid="51" duration="110"/>
   <frame tileid="52" duration="110"/>
  </animation>
 </tile>
 <tile id="44">
  <image source="cavelantern/Campfire_1.png" width="25" height="32"/>
 </tile>
 <tile id="45">
  <image source="cavelantern/Campfire_2.png" width="25" height="32"/>
 </tile>
 <tile id="46">
  <image source="cavelantern/Campfire_3.png" width="25" height="32"/>
 </tile>
 <tile id="47">
  <image source="cavelantern/Campfire_4.png" width="25" height="32"/>
 </tile>
 <tile id="48">
  <image source="cavelantern/Campfire_5.png" width="25" height="32"/>
 </tile>
 <tile id="49">
  <image source="cavelantern/Campfire_6.png" width="25" height="32"/>
 </tile>
 <tile id="50">
  <image source="cavelantern/Campfire_7.png" width="25" height="32"/>
 </tile>
 <tile id="51">
  <image source="cavelantern/Campfire_8.png" width="25" height="32"/>
 </tile>
 <tile id="52">
  <image source="cavelantern/Campfire_9.png" width="25" height="32"/>
 </tile>
</tileset>
