import 'dart:async';

import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';
import 'package:angular_router/angular_router.dart';

import '../model/user.dart';
import '../services/user_service.dart';


@Component(
  selector: 'ng-account',
  directives: const [CORE_DIRECTIVES, ROUTER_DIRECTIVES, formDirectives],
  templateUrl: 'account_component.html',
  providers: const [UserService],
)
class AccountComponent implements OnInit{

  bool editEmail = false;
  bool editPassword = false;

  // todo implement username and email changing
  User user = new User();

  final UserService _userService;
  AccountComponent(this._userService);

  @override
  ngOnInit(){
    user.username="Please wait...";
    user.email="Please wait...";
    user.password = "";

    getAccount();
  }

  Future<Null> getAccount() async{ //Future<Null> is void for asynchronous tasks
    user = await _userService.getUser();
  }

  void editUser(){
    _userService.editUser(user);
    editEmail = false;
    editPassword = false;
    user.password = "";

  }

}