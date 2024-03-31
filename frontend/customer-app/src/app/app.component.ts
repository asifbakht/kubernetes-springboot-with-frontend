<<<<<<< HEAD
import { Component } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatDividerModule } from "@angular/material/divider";
import { RouterModule, RouterOutlet } from "@angular/router";
=======
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { RouterModule, RouterOutlet } from '@angular/router';
>>>>>>> 4b522b5b9fcc6bf1e535a23c0be6a60c4976483d

@Component({
  selector: "app-root",
  standalone: true,
  imports: [RouterOutlet, MatDividerModule, MatButtonModule, RouterModule],
<<<<<<< HEAD
  templateUrl: "./app.component.html",
  styleUrl: "./app.component.css"
=======
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
>>>>>>> 4b522b5b9fcc6bf1e535a23c0be6a60c4976483d
})
export class AppComponent {
  title = "payment-app";
}
