import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { PagedResources } from '../utils/base/paged.resources';
import { Constants } from '../utils/common/constants';
import { BaseService } from '../utils/services/base.service';
import { Customer } from './customer';

@Injectable({
  providedIn: 'root',
})
export class CustomerService extends BaseService {
  constructor(private _http: HttpClient) {
    super(_http);
  }

  public getByCustomerId(customerId: string): Observable<Customer[]> {
    customerId = customerId.trim();
    const options = customerId
      ? {
          params: new HttpParams().set(Constants.CUSTOMER_ID, customerId),
        }
      : {};
    return this.http //
      .get<Customer[]>(this.apiURL, options)
      .pipe(catchError(this.errorHandler));
  }

  public findAll(params: any): Observable<PagedResources<Customer>> {
    const page = params?.page || 0;
    const size = params?.size || 10;
    const sort = params?.sort || Constants.API_SORT_ASC;

    const options = {
      params: new HttpParams()
        .set(Constants.API_PAGE, page)
        .set(Constants.API_SIZE, size)
        .set(Constants.API_SORT, sort),
    };
    return this.http //
      .get<PagedResources<Customer>>(`${this.apiURL}/all`, options)
      .pipe(catchError(this.errorHandler));
  }

  public saveCustomer(customer: Customer): Observable<Customer> {
    return this.http
      .post<Customer>(this.apiURL, customer, this.httpOptions)
      .pipe(catchError(this.errorHandler));
  }

  public updateCustomer(customer: Customer): Observable<Customer> {
    return this.http
      .put<Customer>(
        `${this.apiURL}/${customer.id}`,
        customer,
        this.httpOptions
      )
      .pipe(catchError(this.errorHandler));
  }

  public deleteCustomer(customerId: string): Observable<unknown> {
    return this.http
      .delete(`${this.apiURL}/${customerId}`)
      .pipe(catchError(this.errorHandler));
  }
}
