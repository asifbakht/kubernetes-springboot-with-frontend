import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders
} from "@angular/common/http";
import { throwError } from "rxjs";
import { environment } from "../../../environment/environment";
import { Constants } from "../common/constants";

export class BaseService {
  protected apiURL: string;
  protected httpOptions: object = {
    headers: new HttpHeaders(Constants.HEADER_CONTENT_TYPE_JSON)
  };

  constructor(protected http: HttpClient) {
    this.apiURL = environment.apiUrl;
  }

  public errorHandler(error: HttpErrorResponse) {
    return throwError(() => new Error(error.message));
  }
}
