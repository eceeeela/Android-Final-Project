package com.example.real_timedbaccount;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ReturnBookActivity extends AppCompatActivity {

    private GridView gridView;
    private ReturnBookAdapter adapter;
    private ArrayList<Book> returnList;
    private Button btnReturnSelected;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_book);

        gridView = findViewById(R.id.book_returnGridView);
        btnReturnSelected = findViewById(R.id.btnReturnSelected);

        returnList = new ArrayList<>();
        adapter = new ReturnBookAdapter(this, returnList);
        gridView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("borrowed_books");

        loadReturnableBooks();

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Book selectedBook = returnList.get(position);
            selectedBook.setSelected(!selectedBook.isSelected());
            adapter.notifyDataSetChanged();
        });

        btnReturnSelected.setOnClickListener(v -> {
            ArrayList<Book> selectedBooks = new ArrayList<>();
            for (Book book : returnList) {
                if (book.isSelected()) {
                    selectedBooks.add(book);
                }
            }

            if (selectedBooks.isEmpty()) {
                Toast.makeText(this, "No books selected to return.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (Book book : selectedBooks) {
                returnBook(book);
            }

            returnList.removeAll(selectedBooks);
            adapter.notifyDataSetChanged();
        });
    }

    private void loadReturnableBooks() {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if (book != null) {
                        returnList.add(book);
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load books.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void returnBook(Book book) {
        databaseReference.child(book.getIsbn()).removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Returned: " + book.getTitle(), Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to return: " + book.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }
}
