import { Routes } from "@angular/router";
import { CustomerComponent } from "./customer/customer.component";
import { UpdateCustomerComponent } from "./customer/update-customer/update-customer.component";

export const routes: Routes = [
  // {
  //   path: "customer",
  //   loadComponent: () => CustomerComponent,
  //   children: [
  //     {
  //       path: "update-customer",
  //       loadComponent: () => UpdateCustomerComponent
  //     }
  //   ]
  // }
  {
    path: "customer",
    pathMatch: "full",
    loadComponent: () => CustomerComponent
  },
  {
    pathMatch: "full",
    path: "update-customer",
    loadComponent: () => UpdateCustomerComponent
  }
];
