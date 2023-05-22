<?php

namespace App\Http\Controllers;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class MainController extends Controller
{
    public function register(Request $request)
    {
        $validatedData = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:6',
        ]);
        $user = User::create([
            'name' => $validatedData['name'],
            'email' => $validatedData['email'],
            'password' => bcrypt($validatedData['password']),
        ]);
        return response()->json(['user' => $user], 201);
    }

    public function login(Request $request)
    {
        $validatedData = $request->validate([
            'email' => 'required|string|email',
            'password' => 'required|string',
        ]);
        
        $user = User::where('email', $validatedData['email'])->first();
        if (!$user || !Hash::check($validatedData['password'], $user->password)) {
            return response()->json(['error' => 'Unauthorized'], 401);
        }
        $token = $user->createToken('access_token');
        return response()->json(['user' => $user, 'access_token' => $token->plainTextToken], 201);
    }
}
