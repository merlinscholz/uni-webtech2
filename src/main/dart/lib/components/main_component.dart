import 'dart:async';

import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';
import 'package:angular_router/angular_router.dart';
import 'package:cookie/cookie.dart' as cookie;

import '../model/item.dart';
import '../model/tag.dart';
import '../services/item_service.dart';
import '../services/tag_service.dart';
import 'list-item_component.dart';
import 'new-item_component.dart';
import 'tag-nav_component.dart';

@Component(
  selector: 'ng-main',
  directives: const [CORE_DIRECTIVES, ROUTER_DIRECTIVES, formDirectives, NewItemComponent,  ListItemComponent, TagNavComponent],
  templateUrl: 'main_component.html',
  providers: const [ItemService, TagService],
)
class MainComponent implements OnInit{

  // List Items and Tags
  List<Item> items = [];
  List<Tag> tags = [];

  // Services
  final ItemService _itemService;
  final TagService _tagService;
  MainComponent(this._itemService, this._tagService);

  // Filtering
  String searchQuery="";

  // Active
  String activeOwner = null;
  String activeTime = "current";

  bool itemsIsEmpty(){
    return items.isEmpty;
  }

  @override
  ngOnInit(){
    _itemService.setUserFilter(cookie.get("username"));
    initialize();
  }


  void initialize(){
    getItems();
    getTags();
  }


  Future<Null> getItems() async{ //Future<Null> is void for asynchronous tasks
    items = await _itemService.get();
  }


  void setTagFilter(Tag filter){
    if(filter==null){
      _itemService.removeTagFilter();
    }else{
      _itemService.setTagFilter(filter);
    }
    getItems();
  }

  void setTimeFilter(String filter){
    activeTime = filter;
    _itemService.setTimeFilter(filter);
    getItems();
  }

  void setSearchFilter(){
    if(searchQuery==null||searchQuery==""){
      _itemService.removeSearchFilter();
    }else {
      _itemService.setSearchFilter(searchQuery);
    }
    getItems();
  }

  void setOwnerFilter(String owner){
    activeOwner = owner;
    if(owner==null||owner==""){
      _itemService.removeAssigneeFilter();
      _itemService.removeOwnerFilter();
    }else if(owner=="me"){
      _itemService.setOwnerFilter(cookie.get("username"));
      _itemService.removeAssigneeFilter();
    }else if(owner=="notme"){
      _itemService.removeOwnerFilter();
      _itemService.setAssigneeFilter(cookie.get("username"));
    }
    getItems();
  }

  Future<Null> getTags() async{ //Future<Null> is void for asynchronous tasks
    tags = await _tagService.getTags();
  }
}