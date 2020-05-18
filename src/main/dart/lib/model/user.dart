class User{
  String username;
  String email;
  String password;

  Map toJson(){
    Map map = new Map();
    map["username"]=username;
    map["email"]=email;
    map["password"]=password;
    return map;
  }
}