import 'dart:convert';
import 'dart:html';

import 'package:angular/angular.dart';
import 'package:cookie/cookie.dart' as cookie;
import 'package:crypto/crypto.dart';

import '../model/user.dart';


@Injectable()
class UserService{
  User getUser(){
    String username = cookie.get('username');

    if(username==null){
      return null;
    }
    if(username=="admin"){
      User adminUser = new User();
      adminUser.username = "admin";
      adminUser.email = "admin@localhost";
      return adminUser;
    }
    User user = new User();

    HttpRequest.request("http://127.0.0.1:8080/tudo/api/users/"+username+"/",method: "GET",withCredentials: true, requestHeaders: {'Accept':'application/json'}).then((response) {
      Map res = JSON.decode(response.responseText);
      user.username = res["username"];
      user.email = res["email"];
    }).catchError((err)=>print("Error loading user detail: "+err));

    return user;
  }

  void editUser(User user){
    if(user.username==null||user.username=="admin"){
      return null;
    }
    if(user.password!=null){
      var bytes = UTF8.encode(user.password);
      String digest = sha256.convert(bytes);
      user.password = digest.toString().toLowerCase();
    }
    HttpRequest.request("http://127.0.0.1:8080/tudo/api/users/"+user.username+"/",method: "PUT",withCredentials: true,sendData: JSON.encode(user), requestHeaders: {'Content-type' : 'application/json', "Access-Control-Allow-Origin": "*", "Access-Control-Allow-Methods": "POST,GET,DELETE,PUT,OPTIONS"});

  }
}