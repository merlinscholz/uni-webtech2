import 'dart:convert';
import 'dart:html';

import 'package:angular/angular.dart';
import 'package:cookie/cookie.dart' as cookie;

import '../model/item.dart';
import '../model/tag.dart';


@Injectable()
class ItemService{

  Tag filterTag = null;
  String filterSearch = null;
  String filterTime = null;
  String filterUser = null;
  String filterOwner = null;
  String filterAssignee = null;

  void setTagFilter(Tag t){
    this.filterTag = t;
  }

  void removeTagFilter(){
    this.filterTag = null;
  }

  void setSearchFilter(String q){
    this.filterSearch = q;
  }

  void removeSearchFilter(){
    this.filterSearch = null;
  }

  void setTimeFilter(String t){
    this.filterTime = t;
  }

  void removeTimeFilter(){
    this.filterTime = null;
  }

  void setUserFilter(String u){
    this.filterUser = u;
  }

  void removeUserFilter(){
    this.filterUser = null;
  }

  void setOwnerFilter(String o){
    this.filterOwner = o;
  }

  void removeOwnerFilter(){
    this.filterOwner = null;
  }

  void setAssigneeFilter(String a){
    this.filterAssignee = a;
  }

  void removeAssigneeFilter(){
    this.filterAssignee = null;
  }

  List<Item> get(){
    List<Item> items = [];

    if(filterUser==null||filterUser==""){
      return new List();
    }

    String url = "http://127.0.0.1:8080/tudo/api/users/"+filterUser+"/items/?";
    if(filterTag!=null)
      url += "tag="+filterTag.id.toString()+"&";
    if(filterTime!=null)
      url += "time="+filterTime+"&";
    if(filterSearch!=null)
      url += "q="+filterSearch+"&";
    if(filterOwner!=null)
      url += "owner="+filterOwner+"&";
    if(filterAssignee!=null)
      url += "assignee="+filterAssignee+"&";

    HttpRequest.request(url,method: "GET",withCredentials: true, requestHeaders: {'Accept':'application/json'}).then((response) {
      List<Map> res = JSON.decode(response.responseText);
      for(Map resItem in res){
        var item = new Item();
        item.id = resItem["id"];
        item.title = resItem["title"];
        item.description = resItem["description"];
        item.done = resItem["done"];
        if(resItem["finished"]!=null)
          item.finished = new DateTime.fromMicrosecondsSinceEpoch(resItem["finished"]*1000);
        if(resItem["created"]!=null)
          item.created = new DateTime.fromMicrosecondsSinceEpoch(resItem["created"]*1000);
        if(resItem["owner"]!=null)
          item.owner = resItem["owner"]["username"];
        if(resItem["assignee"]!=null)
          item.assignee = resItem["assignee"]["username"];
        item.tags = [];
        for(Map resTag in resItem["tags"]){
          var tag = new Tag();
          tag.id = resTag["id"];
          tag.name = resTag["name"];
          tag.color = resTag["color"];
          item.tags.add(tag);
        }
        items.add(item);
      }
    }).catchError((err)=>print("Error loading items: "+err));
    return items;
  }

  void addItem(Item item){
    Map newItem = new Map();
    if(item.title!=null){
      newItem["title"] = item.title;
    }
    if(item.description!=null){
      newItem["description"] = item.description;
    }
    if(item.done!=null){
      newItem["done"] = item.done;
    }
    if(item.tags!=null){
      newItem["tags"] = [];
      for(Tag t in item.tags){
        newItem["tags"].add(t.id);
      }
    }
    if(item.owner!=null){
      newItem["owner"] = item.owner;
    }
    if(item.assignee!=null){
      newItem["assignee"] = item.assignee;
    }

    String username = cookie.get('username');
    if(username==null){
      return null;
    }
    if(username=="admin"){
      return null;
    }
    print(JSON.encode(newItem));
    HttpRequest.request("http://127.0.0.1:8080/tudo/api/users/"+username+"/items",method: "POST",withCredentials: true,sendData: JSON.encode(newItem), requestHeaders: {'Content-type' : 'application/json', "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Methods": "POST,GET,DELETE,PUT,OPTIONS"});

  }

  void updateItem(Item item){
    Map newItem = new Map();
    if(item.title!=null){
      newItem["title"] = item.title;
    }
    if(item.description!=null){
      newItem["description"] = item.description;
    }
    if(item.done!=null){
      newItem["done"] = item.done;
    }
    if(item.tags!=null){
      newItem["tags"] = [];
      for(Tag t in item.tags){
        newItem["tags"].add(t.id);
      }
    }
    if(item.owner!=null){
      newItem["owner"] = item.owner;
    }
    if(item.assignee!=null){
      newItem["assignee"] = item.assignee;
    }

    String username = cookie.get('username');
    String idString = item.id.toString();
    if(username==null){
      return null;
    }
    if(username=="admin"){
      return null;
    }
    print(JSON.encode(newItem));
    HttpRequest.request("http://127.0.0.1:8080/tudo/api/users/"+item.owner+"/items/"+idString,method: "PUT",withCredentials: true,sendData: JSON.encode(newItem), requestHeaders: {'Content-type' : 'application/json', "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Methods": "POST,GET,DELETE,PUT,OPTIONS"});
  }

  void addTag(Item item, Tag tag){
    item.tags.add(tag);
    updateItem(item);
  }

  void removeTag(Item item, Tag tag) {
    if(item.tags.contains(tag)){
      item.tags.remove(tag);
    }
    updateItem(item);
  }

  void deleteItem(Item item){
    String username = cookie.get('username');
    String idString = item.id.toString();
    if(username==null){
      return null;
    }
    if(username=="admin"){
      return null;
    }    HttpRequest.request("http://127.0.0.1:8080/tudo/api/users/"+item.owner+"/items/"+idString,method: "DELETE",withCredentials: true, requestHeaders: {"Access-Control-Allow-Origin": "*", "Access-Control-Allow-Methods": "POST,GET,DELETE,PUT,OPTIONS"});
  }
}