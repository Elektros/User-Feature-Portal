import {Component, OnInit} from "@angular/core";
import {DjiImages} from "../../../assets/images/images.enum";

@Component({
  selector: 'app-dji-store',
  templateUrl: './dji-store.component.html',
  styleUrls: ['./dji-store.component.scss']
})

export class DjiStoreComponent implements OnInit {
  constructor() {
  }

  ngOnInit() {
    const imageElement = document.getElementById('imageElement') as HTMLImageElement;
    imageElement.src = DjiImages.mini;

    const imageElement1 = document.getElementById('imageElement1') as HTMLImageElement;
    imageElement1.src = DjiImages.mavic;

    const imageElement2 = document.getElementById('imageElement2') as HTMLImageElement;
    imageElement2.src = DjiImages.air;

    const imageElement3 = document.getElementById('imageElement3') as HTMLImageElement;
    imageElement3.src = DjiImages.avata;

    const imageElement4 = document.getElementById('imageElement4') as HTMLImageElement;
    imageElement4.src = DjiImages.phantom;

    const imageElement5 = document.getElementById('imageElement5') as HTMLImageElement;
    imageElement5.src = DjiImages.inspire;

    const imageElement6 = document.getElementById('imageElement6') as HTMLImageElement;
    imageElement6.src = DjiImages.thermal;
  }

  protected readonly onpointerout = onpointerout;
}

