<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\Book;

class SaveBookController extends Controller
{
    public function Savebook(Request $request)
    {
        $book_name = $request->input('book_name');
        $language = $request->input('language');
        $release_date = $request->input('release_date');
        $page_count = $request->input('page_count');
        $uuid = $request->input('uuid');

        $book = new Book();
        $book->book_title = $book_name;
        $book->language = $language;
        $book->page_count = $page_count;
        $book->release_date = $release_date;
        $book->uuid = $uuid;
        $book->save();

        return [
            "status" => 'save success',
        ];
    }

    public function Display_Saved()
    {
        $savings = Book::all();

        return response()->json([
            'savings' => $savings,
        ]);
    }

    public function Delete_Book($id)
    {
        Book::find($id)->delete();

        return response()->json([
            "status" => 'delete success',
        ]);
    }

    public function getSimilarBooks($uuid)
    {
        $similarBooks = Book::where('uuid', 'like', '%' . $uuid . '%')->get();
        return response()->json([
            'success' => true,
            'data' => $similarBooks,
        ]);
    }
}
