package com.example.real_timedbaccount;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class BorrowBookActivity extends AppCompatActivity {
    private GridView gridView;
    private SearchView searchView;
    private ArrayList<Book> booksList;
    private ArrayList<Book> cartList;
    private ArrayList<Book> returnBookList;
    private ArrayList<Book> bookshelfList;
    private BookAdapter bookAdapter;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_book);

        // Initialize views
        gridView = findViewById(R.id.gridView);
        searchView = findViewById(R.id.searchView);
        Button viewCartButton = findViewById(R.id.btn_viewCart);

        // Initialize lists
        booksList = new ArrayList<>();
        cartList = new ArrayList<>();
        returnBookList = new ArrayList<>();
        bookshelfList = new ArrayList<>();

        // Initialize the adapter with updated constructor
        bookAdapter = new BookAdapter(this, booksList, cartList, returnBookList, bookshelfList);
        gridView.setAdapter(bookAdapter);

        // Initialize request queue
        requestQueue = Volley.newRequestQueue(this);

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query == null || query.trim().isEmpty()) {
                    Toast.makeText(BorrowBookActivity.this, "Please enter a valid search term", Toast.LENGTH_SHORT).show();
                    return false;
                }
                booksList.clear(); // Clear previous search results
                searchBooks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // View Cart button click
        viewCartButton.setOnClickListener(v -> {
            Intent intent = new Intent(BorrowBookActivity.this, CartActivity.class);
            intent.putExtra("cartList", cartList); // Pass cartList
            startActivity(intent);
        });
    }

    // Method to fetch books based on search query
    private void searchBooks(String query) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, response -> {
            try {
                JSONArray items = response.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject bookObject = items.getJSONObject(i);
                    JSONObject volumeInfo = bookObject.getJSONObject("volumeInfo");

                    String title = volumeInfo.getString("title");
                    String author = volumeInfo.getJSONArray("authors").getString(0);
                    String isbn = volumeInfo.getJSONArray("industryIdentifiers").getJSONObject(0).getString("identifier");
                    String imageURL = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                    String synopsis = volumeInfo.optString("description", "No description available");
                    String previewLink = volumeInfo.getString("previewLink");

                    Book book = new Book(title, author, isbn, imageURL, synopsis, previewLink, null);
                    booksList.add(book);
                }

                bookAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> error.printStackTrace());

        requestQueue.add(jsonObjectRequest);
    }
}
