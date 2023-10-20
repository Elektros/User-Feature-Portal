import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from "@angular/router";
import {BmiComponent} from "../components/bmi/bmi.component";
import {UserComponent} from "../components/user/user.component";
import {LoggingComponent} from "../components/logging/logging.component";
import {HomeComponent} from "../components/home/home.component";
import {BookComponent} from "../components/book/book.component";
import {BibleComponent} from "../components/bibellese/bible.component";
import {CalculatorComponent} from "../components/calculator/calculator.component";
import {DjiStoreComponent} from "../components/djiStore/dji-store.component";
import {DjiMavicComponent} from "../components/djiStore/DjiMavic/dji-Mavic.component";
import {DjiAirComponent} from "../components/djiStore/DjiAir/dji-Air.component";
import {DjiAvataComponent} from "../components/djiStore/DjiAvata_and_FPV/dji-Avata.component";
import {DjiPhantomComponent} from "../components/djiStore/Phantom/dji-Phantom.component";
import {DjiInspireComponent} from "../components/djiStore/Inspire/dji-Inspire.component";
import {DjiThermalComponent} from "../components/djiStore/Thermal/dji-Thermal.component";
import {DjiMiniComponent} from "../components/djiStore/DjiMini/dji-mini.component";

const routes: Routes = [
  {path: 'app', redirectTo: 'home'},
];

const routesBmi: Routes = [
  {path: 'bmi', component: BmiComponent}
];

const routesBook: Routes = [
  {path: 'book', component: BookComponent}
]

const routesUser: Routes = [
  {path: 'user', component: UserComponent}
];

const routesLogging: Routes = [
  {path: 'logging', component: LoggingComponent}
];

const routesHome: Routes = [
  {path: 'home', component: HomeComponent}
];

const routesBible: Routes = [
  {path: 'bible', component: BibleComponent}
];

const routesCalculator: Routes = [
  {path: 'calculator', component: CalculatorComponent}
]

const routesDjiStore: Routes = [
  {path: 'DjiStore', component: DjiStoreComponent}
]

const routesDjiMini: Routes = [
  {path: 'djiMini', component: DjiMiniComponent}
]

const routesDjiMavic: Routes = [
  {path: 'djiMavic', component: DjiMavicComponent}
  ]

const routesDjiAir: Routes = [
  {path: 'DjiAir', component: DjiAirComponent}
]

const routesDjiAvata: Routes = [
  {path: 'DjiAvata', component: DjiAvataComponent}
  ]

const routesDjiPhantom: Routes = [
  {path: 'DjiPhantom', component: DjiPhantomComponent}
]

const routesDjiInspire: Routes = [
  {path: 'DjiInspire', component: DjiInspireComponent}
]

const routesDjiThermal: Routes = [
  {path: 'DjiThermal', component: DjiThermalComponent}
]

const different: Routes = [
  {path: '**', redirectTo: 'home'},
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forChild(routesBmi),
    RouterModule.forChild(routesBook),
    RouterModule.forChild(routesUser),
    RouterModule.forChild(routesLogging),
    RouterModule.forChild(routesHome),
    RouterModule.forChild(routesBible),
    RouterModule.forChild(routesCalculator),
    RouterModule.forChild(routesDjiStore),
    RouterModule.forChild(routesDjiMini),
    RouterModule.forChild(routesDjiMavic),
    RouterModule.forChild(routesDjiAir),
    RouterModule.forChild(routesDjiAvata),
    RouterModule.forChild(routesDjiInspire),
    RouterModule.forChild(routesDjiPhantom),
    RouterModule.forChild(routesDjiThermal),
    RouterModule.forChild(different)
  ],
  exports: [
    CommonModule,
    RouterModule
  ]
})
export class RoutingModule {
}
