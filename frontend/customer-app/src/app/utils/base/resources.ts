/**
 * This class provides a wrapper for a collection of entities
 */
export interface Resources<T> {
  content: T[];
  statusCode: number;
}
