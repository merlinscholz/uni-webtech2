import 'dart:convert';

import 'tag.dart';

class Item{
  int id;
  String title;
  String description;
  bool done;
  DateTime finished;
  DateTime created;
  String owner;
  String assignee;
  List<Tag> tags;

  Map toJson(){
    Map map = new Map();
    map["id"]=id;
    map["title"]=title;
    map["description"]=description;
    map["done"]=done;
    map["finished"]=finished;
    map["created"]=created;
    map["owner"]=owner;
    map["assignee"]=assignee;
    map["tags"]=JSON.encode(tags);
    return map;
  }
}