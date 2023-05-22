<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\MainController;
use App\Http\Controllers\SaveBookController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
return $request->user();});

// Register and Login routes (only API).
Route::post('v1/register', [MainController::class,'register']);
Route::post('v1/login', [MainController::class,'login']);


// Books API endpoint.
Route::post('v1/save', [SaveBookController::class,'Savebook']);
Route::get('v1/display', [SaveBookController::class,'Display_Saved']);
Route::get('v1/delete/{id}', [SaveBookController::class,'Delete_Book']);