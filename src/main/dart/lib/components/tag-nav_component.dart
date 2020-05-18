import 'dart:async';

import 'package:angular/angular.dart';
import 'package:angular_forms/angular_forms.dart';

import '../model/tag.dart';
import '../services/tag_service.dart';

@Component(
  selector: 'ng-tag-nav',
  directives: const [CORE_DIRECTIVES, formDirectives],
  templateUrl: 'tag-nav_component.html',
  providers: const [TagService],
)
class TagNavComponent implements OnInit{

  @Input()
  List<Tag> tags;

  final _onTagSelect = new StreamController<Tag>();
  @Output()
  Stream<Tag> get onTagSelect => _onTagSelect.stream;

  final _refreshTags  = new StreamController();
  @Output()
  Stream get refreshTags => _refreshTags.stream;

  // New Tag
  Tag newTag;

  final TagService _tagService;
  TagNavComponent(this._tagService);

  Map colors = Tag.colors;

  @override
  ngOnInit(){
    initialize();
  }


  void initialize(){
    _refreshTags.add(null);
    newTag = new Tag();
    newTag.name="";
    newTag.color=colors["Gray"];
    newTag.items=0;
  }

  void select(Tag t){
    _onTagSelect.add(t);
  }

  Future<Null> newTagOnSubmit() async{
    _tagService.addTag(newTag);
    new Timer(const Duration(milliseconds: 500), ()=>initialize()); // todo replace this shit

  }

  Future<Null> deleteTag(Tag t) async{  // todo refresh items
    _tagService.deleteTag(t);
    tags.remove(t);
    new Timer(const Duration(milliseconds: 500), ()=>_refreshTags.add(null));
  }

}
