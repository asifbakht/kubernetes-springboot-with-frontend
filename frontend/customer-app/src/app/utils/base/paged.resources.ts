import { Resources } from "./resources";

/**
 * This class provides model for a paged resource
 *
 */

export interface PagedResources<T> extends Resources<T> {
  totalRecords: number;
  totalPages: number;
  currentPage: number;
}
