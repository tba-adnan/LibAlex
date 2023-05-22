<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Book;

class SaveBookController extends Controller
{
    public function Savebook(Request $request) {

    $book_name = $request->input('book_name');
    // $author = $request->input('author');
    $language = $request->input('language');
    $release_date = $request->input('release_date');
    $page_count = $request->input('page_count');
    
    $book = new Book();
    $book->book_title = $book_name;
    $book->author = 'null' ;
    $book->language = $language;
    $book->page_count = $page_count;
    $book->release_date = $release_date;
    $book->save();
    // 
    return [
        "status" => 'save success',
    ];
    }

 public function Display_Saved() {
    $savings = Book::all();
    return response()->json([
        'savings' => $savings,
    ]);

 }


 public function Delete_Book($id) {
    Book::find($id)->delete();
    return response()->json([
        "status" => 'delete success',
    ]);

 }


}
