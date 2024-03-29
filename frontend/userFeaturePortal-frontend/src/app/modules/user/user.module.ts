import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {StoreModule} from "@ngrx/store";
import {EffectsModule} from "@ngrx/effects";
import {UserEffects} from "./user.effects";
import {UserFacade} from "./user.facade";
import {UserReducer} from "./user.reducer";
import {FeatureManager} from "../../../assets/utils/feature.manager";
import {USER_FEATURE_NAME} from "./userState";

@NgModule({
  declarations: [],
  imports: [
    CommonModule, StoreModule.forRoot({}),
    StoreModule.forFeature(USER_FEATURE_NAME, UserReducer),
    EffectsModule.forRoot(), EffectsModule.forFeature([UserEffects])],
  providers: [UserFacade, FeatureManager]
})
export class UserModule {
}
