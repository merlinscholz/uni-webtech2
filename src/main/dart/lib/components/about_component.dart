import 'package:angular/angular.dart';
import 'package:angular_router/angular_router.dart';

@Component(
  selector: 'ng-about',
  directives: const [CORE_DIRECTIVES, ROUTER_DIRECTIVES],
  templateUrl: 'about_component.html',
)
class AboutComponent{

  AboutComponent(){
  }
}