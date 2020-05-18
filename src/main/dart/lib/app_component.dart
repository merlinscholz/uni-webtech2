import 'package:angular/core.dart';
import 'package:angular_router/angular_router.dart';

import 'components/about_component.dart';
import 'components/account_component.dart';
import 'components/main_component.dart';


@Component(
  directives: const [ROUTER_DIRECTIVES],
  selector: 'ng-app',
  template: '''
      <router-outlet></router-outlet>
   ''',
)
@RouteConfig(const [
  const Route(path: '/', name: 'Main', component: MainComponent),
  const Route(path: '/account', name: 'Account', component: AccountComponent),
  const Route(path: '/about', name: 'About', component: AboutComponent)
])
class AppComponent {
}