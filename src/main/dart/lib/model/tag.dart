class Tag{
  int id;
  String name;
  String color;
  int items;

  Map toJson(){
    Map map = new Map();
    map["id"]=id;
    map["name"]=name;
    map["color"]=color;
    return map;
  }

  static Map get colors{
    Map m = new Map();
    m["Lime"] = "a4c400";
    m["Green"] = "60a917";
    m["Blue"] = "0050ef";
    m["Violet"] = "aa00ff";
    m["Red"] = "e51400";
    m["Orange"] = "fa6800";
    m["Gray"] = "333333";
    return m;
  }
}