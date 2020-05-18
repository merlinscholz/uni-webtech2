import 'dart:async';

import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';

import '../model/item.dart';
import '../model/tag.dart';
import '../services/item_service.dart';

@Component(
  selector: 'ng-new-item',
  directives: const [CORE_DIRECTIVES, formDirectives],
  templateUrl: 'new-item_component.html',
  providers: const [ItemService],
)
class NewItemComponent implements OnInit{

  // New Item
  bool showItemAddDetails;
  Item newItem;
  bool newItemEmpty() => newItem.title=="";
  Tag newItemTag;

  @Input()
  List<Tag> tags;

  final _refreshItems  = new StreamController();
  @Output()
  Stream get refreshItems => _refreshItems.stream;


  // Services
  final ItemService _itemService;
  NewItemComponent(this._itemService);

  @override
  ngOnInit(){
    newItem = new Item();
    newItem.title="";
    newItem.tags = [];
  }


  void initialize(){
    _refreshItems.add(0);
    newItem = new Item();
    newItem.title="";
    newItem.tags = [];
  }

  Future<Null> newItemOnSubmit() async{
    _itemService.addItem(newItem);
    new Timer(const Duration(milliseconds: 500), ()=>initialize());
  }

  void newItemAddTag(){
    new Future((){
      if(!newItem.tags.contains(newItemTag)) {
        newItem.tags.add(newItemTag);
      }
    });
  }

  void newItemRemoveTag(Tag t){
    newItem.tags.remove(t);
  }
}