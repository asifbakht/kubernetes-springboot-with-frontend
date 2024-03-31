import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { DialogConfirmComponent } from '../common/dialog/confirmation/dialog.confirm.component';
import { PageMetadata } from '../utils/base/paged.metadata';
import { PagedResources } from '../utils/base/paged.resources';
import { Constants } from '../utils/common/constants';
import { Customer } from './customer';
import { CustomerService } from './customer.service';
@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [
    RouterModule,
    MatSortModule,
    MatIconModule,
    MatTableModule,
    MatButtonModule,
    HttpClientModule,
    MatPaginatorModule,
    MatTooltipModule,
    DatePipe,
  ],
  providers: [CustomerService],
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.css',
})
export class CustomerComponent implements OnInit, OnDestroy, AfterViewInit {
  public defaultSort: string = 'name';
  public defaultSize: number = 10;

  public pageData: PageMetadata | undefined;
  public dataSourceCustomer: MatTableDataSource<Customer>;
  public columnNames: string[] = [
    'name',
    'email',
    'dateOfBirth',
    'phoneNumber',
    'itinOrSsn',
    'edit',
    'payments',
  ];
  @ViewChild(MatSort)
  private sort!: MatSort;
  private sortDirection: string = Constants.API_SORT_ASC;
  private activeSort: string = this.defaultSort;
  private componentDestroyed$: Subject<boolean>;

  constructor(
    private customerService: CustomerService,
    public dialog: MatDialog
  ) {
    this.componentDestroyed$ = new Subject();
    this.dataSourceCustomer = new MatTableDataSource(new Array());
  }

  ngOnInit(): void {
    this.fetchAll({
      page: 0,
      size: this.defaultSize,
      sort: Constants.API_SORT_ASC,
    });
  }

  ngOnDestroy() {
    this.componentDestroyed$.next(true);
    this.componentDestroyed$.complete();
  }

  ngAfterViewInit() {
    this.dataSourceCustomer.sort = this.sort;
    this.dataSourceCustomer.sortingDataAccessor = (
      item: any,
      property: string
    ) => {
      switch (property) {
        case 'name':
          return item.firstName + item.lastName;
        case 'dateOfBirth':
          return new Date(item.dateOfBirth);
        default:
          return item[property];
      }
    };
  }

  private fetchAll(page: object): void {
    this.customerService
      .findAll(page)
      .pipe(takeUntil(this.componentDestroyed$))
      .subscribe((data: PagedResources<Customer>) => {
        this.dataSourceCustomer.data = data.content;
        this.pageData = data;
      });
  }

  public pageChanged(event: PageEvent): void {
    if (this.activeSort === this.defaultSort)
      this.activeSort = `firstName,lastName`;
    this.fetchAll({
      page: event.pageIndex,
      size: event.pageSize,
      sort: `${this.activeSort},${this.sortDirection}`,
    });
  }

  public sortData(event: Sort): void {
    this.sortDirection = event.direction;
    this.activeSort = event.active;
    const pageEvent = new PageEvent();
    pageEvent.length = this.pageData?.totalPages ?? 10;
    pageEvent.pageSize = this.defaultSize;
    this.pageChanged(pageEvent);
  }

  public deleteCustomer(customer: Customer): void {
    let dialogRef = this.dialog.open(DialogConfirmComponent, {
      width: '250px',
      data: {
        title: 'Customer',
        resourceName: `${customer.firstName} ${customer.lastName}`,
      },
    });

    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.customerService
          .deleteCustomer(customer.id)
          .subscribe((response: any) => {
            this.fetchAll({
              page: 0,
              size: this.defaultSize,
              sort: Constants.API_SORT_ASC,
            });
          });
      }
    });
  }
}
