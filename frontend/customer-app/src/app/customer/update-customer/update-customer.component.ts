import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Router, RouterModule } from '@angular/router';
import { Customer } from '../customer';
import { CustomerService } from '../customer.service';

/**
 * This class implements customer crud operation
 *
 * @author Asif Bakht
 * @since 1.0
 */
@Component({
  selector: 'update-customer',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatIconModule,
    MatSnackBarModule,
    MatButtonModule,
    CommonModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatGridListModule,
    RouterModule,
  ],
  providers: [CustomerService],
  templateUrl: './update-customer.component.html',
  styleUrls: ['./update-customer.component.css'],
})
export class UpdateCustomerComponent implements OnInit {
  public customerForm = new FormGroup<Customer>({
    id: new FormControl(''),
    firstName: new FormControl(undefined, [
      Validators.required,
      Validators.pattern('[a-zA-Z][a-zA-Z ]+'),
    ]),
    lastName: new FormControl(''),
    email: new FormControl(''),
    dateOfBirth: new FormControl(''),
    phoneNumber: new FormControl(''),
    itinOrSsn: new FormControl(''),
    // id: new FormControl(),
    // firstName: new FormControl('', [
    //   Validators.required,
    //   Validators.pattern('[a-zA-Z][a-zA-Z ]+'),
    // ]),
    // lastName: new FormControl('', [
    //   Validators.required,
    //   Validators.pattern('[a-zA-Z][a-zA-Z ]+'),
    // ]),
    // email: new FormControl('', [Validators.required, Validators.email]),
    // dateOfBirth: new FormControl(),
    // phoneNumber: new FormControl(),
    // itinOrSsn: new FormControl(),
  });
  public errorMessage: null | string;

  private customerData!: Customer;
  private isCreate: boolean = true;

  constructor(
    private router: Router,
    private customerService: CustomerService
  ) {
    this.errorMessage = null;
  }

  ngOnInit() {
    this.customerData =
      typeof window !== 'undefined' ? window.history?.state?.data : null;
    if (this.customerData) {
      this.isCreate = false;
      this.customerForm.setValue({
        id: this.customerData.id,
        firstName: this.customerData.firstName,
        lastName: this.customerData.lastName,
        email: this.customerData.email,
        dateOfBirth: new Date(this.customerData.dateOfBirth),
        phoneNumber: this.customerData.phoneNumber,
        itinOrSsn: this.customerData.itinOrSsn,
      });
    }
  }

  onSubmit() {
    if (this.customerForm.invalid) {
      this.errorMessage = 'Please fill the form properly';
      return;
    }
    this.errorMessage = '';
    if (this.isCreate) {
      this.customerService
        .saveCustomer(this.customerForm.value)
        .subscribe((customer: Customer) => {
          this.navigateBack();
        });
    } else {
      this.customerService
        .updateCustomer(this.customerForm.value)
        .subscribe((updatedCustomer: Customer) => {
          this.navigateBack();
        });
    }
  }
  public navigateBack(): void {
    this.router.navigate(['../customer']);
  }
}
