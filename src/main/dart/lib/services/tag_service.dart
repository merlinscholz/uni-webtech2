import 'dart:convert';
import 'dart:html';

import 'package:angular/angular.dart';
import 'package:cookie/cookie.dart' as cookie;

import '../model/tag.dart';


@Injectable()
class TagService{
  List<Tag> getTags(){
    List<Tag> tags = [];
    String username = cookie.get('username');
    if(username==null){
      return tags;
    }
    HttpRequest.request("http://127.0.0.1:8080/tudo/api/users/"+username+"/tags",method: "GET",withCredentials: true, requestHeaders: {'Accept':'application/json'}).then((response) {
      List<Map> res = JSON.decode(response.responseText);
      for(Map resTag in res){
        var tag = new Tag();
        tag.id = resTag["id"];
        tag.name = resTag["name"];
        tag.color = resTag["color"];
        tag.items = 0;
        for(Map m in resTag["items"]){
          tag.items++;
        }
        tags.add(tag);
      }
    }).catchError((err)=>print("Error loading items: "+err));
    return tags;
  }

  void addTag(Tag tag) {
    Map newTag = new Map();
    if(tag.name!=null){
      newTag["name"]=tag.name;
    }
    if(tag.color!=null){
      newTag["color"]=tag.color;
    }
    String username = cookie.get('username');
    if(username==null){
      return null;
    }
    if(username=="admin"){
      return null;
    }
    print(JSON.encode(newTag));
    HttpRequest.request("http://127.0.0.1:8080/tudo/api/users/"+username+"/tags",method: "POST",withCredentials: true,sendData: JSON.encode(newTag), requestHeaders: {'Content-type' : 'application/json', "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Methods": "POST,GET,DELETE,PUT,OPTIONS"});

  }
  void deleteTag(Tag tag) {
    String username = cookie.get('username');
    String idString = tag.id.toString();
    if(username==null){
      return null;
    }
    if(username=="admin"){
      return null;
    }
    HttpRequest.request("http://127.0.0.1:8080/tudo/api/users/"+username+"/tags/"+idString,method: "DELETE",withCredentials: true, requestHeaders: {'Content-type' : 'application/json', "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Methods": "POST,GET,DELETE,PUT,OPTIONS"});

  }
}