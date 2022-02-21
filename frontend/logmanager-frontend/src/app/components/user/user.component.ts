import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserFacade} from "../../modules/user/store/user.facade";
import {SubscriptionManager} from "../../../assets/utils/subscription.manager";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit, OnDestroy{

  constructor(private userFacade: UserFacade) {}

  subscriptionManager = new SubscriptionManager();

  dataSource: any;
  displayedColumns: string[] = ['name', 'birthdate', 'weight', 'height', 'favouriteColor', 'bmi', 'delete'];
  listIsEmptyMessage: string = 'There are no users to show!';

  ngOnInit(): void {
    this.subscriptionManager.add(this.userFacade.stateGetUserResponse$).subscribe(result => {
      this.dataSource = result.body
    });
  }

  ngOnDestroy(): void {
    this.subscriptionManager.clear();
  }
}
