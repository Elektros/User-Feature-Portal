import {Component} from "@angular/core";

@Component({
  selector: 'app-calculator',
  templateUrl: './calculator.component.html',
  styleUrls: ['./calculator.component.scss']
})
export class CalculatorComponent {
  constructor() {
  }

  currentInput = "";

  appendToDisplay(value: string): void {
    this.currentInput += value;
    const display = document.getElementById("display") as HTMLInputElement;
    display.value = this.currentInput;
  }

  clearDisplay() {
    this.currentInput = "";
    const display = document.getElementById("display") as HTMLInputElement;
    display.value = "";
  }


  calculate() {
    try {
      console.log(this.currentInput);
      const result = eval(this.currentInput); // Using eval to perform the calculation
      const display = document.getElementById("display") as HTMLInputElement;
      display.value = result.toString();

      this.currentInput = result.toString();
      if (display.value === "Infinity") {
        display.value = "Error";
      }
      if (display.value === "-Infinity") {
        display.value = "Error";
      }


    } catch (error) {
      const display = document.getElementById("display") as HTMLInputElement;
      display.value = "Error";
      this.currentInput = "";
    }
  }

  clearLastDigit() {
    if (this.currentInput.length > 0) {
      this.currentInput = this.currentInput.slice(0, -1);
      // Aktualisiere die Anzeige im Template
      const display = document.getElementById('display') as HTMLInputElement;
      display.value = this.currentInput;
    }
  }



}
