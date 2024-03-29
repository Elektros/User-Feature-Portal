import {Injectable} from "@angular/core";
import {Store} from "@ngrx/store";
import {FavouriteBookState} from "./favouriteBook.state";
import {assignBookToUserAction, deleteFavouriteBookAction, getFavouriteBookAction} from "./favouriteBook.actions";
import {getFavouriteBook} from "./favouriteBook.selector";
import {AddBookRequest} from "../books/addBooks/add-book-request";

@Injectable({providedIn: 'root'})
export class FavouriteBookFacade {
  stateGetFavouriteBookResponse$ = this.favouriteBookState.select(getFavouriteBook)

  constructor(
    private readonly favouriteBookState: Store<FavouriteBookState>
  ) {
  }

  deleteFavouriteBook() {
    this.favouriteBookState.dispatch(deleteFavouriteBookAction())
  }

  getFavouriteBook() {
    this.favouriteBookState.dispatch(getFavouriteBookAction())
  }

  assignBookToUser(request: AddBookRequest) {
    this.favouriteBookState.dispatch(assignBookToUserAction(request))
  }

}
