import 'dart:async';

import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';
import 'package:cookie/cookie.dart' as cookie;

import '../model/item.dart';
import '../model/tag.dart';
import '../services/item_service.dart';

@Component(
  selector: 'ng-list-item',
  directives: const [CORE_DIRECTIVES, formDirectives],
  templateUrl: 'list-item_component.html',
  providers: const [ItemService],
)
class ListItemComponent implements OnInit{

  final ItemService _itemService;
  ListItemComponent(this._itemService);

  @Input()
  Item item;

  @Input()
  List<Tag> tags;

  final _onTagSelect = new StreamController<Tag>();
  @Output()
  Stream<Tag> get onTagSelect => _onTagSelect.stream;

  final _onItemDeletion = new StreamController<Tag>();
  @Output()
  Stream<Tag> get onItemDeletion => _onItemDeletion.stream;

  String getDescription() => item.description==null?'No Description':item.description;
  String getCreated() => item.created==null?'-':'${item.created.day.toString().padLeft(2,'0')}.${item.created.month.toString().padLeft(2,'0')}.${item.created.year.toString().padLeft(2,'0')}, ${item.created.hour.toString().padLeft(2,'0')}:${item.created.minute.toString().padLeft(2,'0')}';
  String getFinished() => item.finished==null?'-':'${item.finished.day.toString().padLeft(2,'0')}.${item.finished.month.toString().padLeft(2,'0')}.${item.finished.year.toString().padLeft(2,'0')}, ${item.finished.hour.toString().padLeft(2,'0')}:${item.finished.minute.toString().padLeft(2,'0')}';


  Tag itemAddTag;
  String itemNewTitle;
  String itemNewDescription;
  String itemNewAssignee;
  bool expanded = false;
  bool editTitle = false;
  bool editDescription = false;
  bool editAssignee = false;

  String username;

  @override
  ngOnInit(){
    username = cookie.get("username");
    itemNewTitle = item.title;
    itemNewDescription = item.description;
  }

  toggleDone(){
    item.done = !item.done;
    if(item.done) {
      item.finished = new DateTime.now();
    }else{
      item.finished = null;
    }
    _itemService.updateItem(item);
  }

  addTag(){   // todo check double tag
    new Future((){
      print(itemAddTag);
      if(!item.tags.contains(itemAddTag)) {
        _itemService.addTag(item, itemAddTag);
      }
    });
  }

  selectTag(Tag t){
    _onTagSelect.add(t);
  }

  removeTag(Tag t){
    _itemService.removeTag(item, t);
  }

  editTitleSubmit(){
    if(itemNewTitle!=item.title&&itemNewTitle!=''){
      item.title = itemNewTitle;
      editTitle = false;
      _itemService.updateItem(item);
    }
  }

  editDescriptionSubmit(){
    if(itemNewDescription!=item.description){
      if(itemNewDescription==""){
        itemNewDescription=null;
      }
      item.description = itemNewDescription;
      _itemService.updateItem(item);
    }
    editDescription = false;
  }

  editAssignSubmit(){
    if(itemNewAssignee!=item.assignee){
      if(itemNewAssignee==""){
        removeAssign();
      }else {
        item.assignee = itemNewAssignee;
        _itemService.updateItem(item);
      }
    }
    editAssignee = false;
  }

  removeAssign(){
    item.assignee = username;
    _itemService.updateItem(item);
    editAssignee = false;
  }

  delete(){
    _itemService.deleteItem(item);
    item = null;
    new Timer(const Duration(milliseconds: 500), ()=>_onItemDeletion.add(null));
  }
}

