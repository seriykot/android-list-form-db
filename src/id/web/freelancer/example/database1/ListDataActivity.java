package id.web.freelancer.example.database1;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ListDataActivity extends Activity {
	private EditText txtNama = null;
	private EditText txtJudul = null;
	private EditText txtISBN = null;

	private Button btnBack = null;
	private Button btnSimpan = null;
	private Button buttonCreate = null;

	private ListView lsvBookView = null;
	private BookAdapter adapter = null;

	private BookDB bookDB = null;
	private ArrayList<Book> listBook = null;

	private Dialog formDialog = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		lsvBookView = (ListView) findViewById(R.id.lsvListBook);

		bookDB = new BookDB(getApplicationContext());
		adapter = new BookAdapter(this);

		refreshListAdapter();
		
		//registerForContextMenu() will register supplied view for onCreateContextMenu() 
		registerForContextMenu(lsvBookView);

		buttonCreate = (Button) findViewById(R.id.buttonCreate);
		buttonCreate.setOnClickListener(new OnClickListener() {

//			@Override
			public void onClick(View arg0) {
				showFormDialog();
			}
		});

		prepareDiaog();
	}

//	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo cmInfo) {
		if (v.getId() == R.id.lsvListBook) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) cmInfo;
			menu.setHeaderTitle(listBook.get(info.position).getJudul());
			String[] menuItems = { "Update", "Delete" };
			for (int menuIndex = 0; menuIndex < menuItems.length; menuIndex++) {
				menu.add(Menu.NONE, menuIndex, menuIndex, menuItems[menuIndex]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		Book book = listBook.get(info.position);

		switch (menuItemIndex) {// taken from menuItems in onCreateContextMenu()
		case 0:
			showFormDialog(book);
			break;
		case 1:
			Log.i("Log", "item " + book.getJudul() + " deleted!");
			bookDB.delete(book);
			refreshListAdapter();
			break;
		}

		return true;
	}

	protected void showFormDialog() {

		txtISBN.setEnabled(true);
		txtISBN.setText("");
		txtJudul.setText("");
		txtNama.setText("");

		formDialog.show();
	}

	protected void showFormDialog(Book book) {

		Log.i("Log", book.toString());
		txtISBN.setEnabled(false);
		txtISBN.setText(book.getISBN());
		txtJudul.setText(book.getJudul());
		txtNama.setText(book.getNamaPenulis());

		formDialog.show();
	}

	/**
	 * We prepare dialog in it's own method so that we can maintain code separation well.
	 * I believe there is another <em>better</em> solution. But, it work for now.
	 */
	private void prepareDiaog() {
		formDialog = new Dialog(ListDataActivity.this);
		formDialog.setContentView(R.layout.form);
		formDialog.setTitle("Form Buku");

		// set dialog width to fill_parent
		LayoutParams formDialogParams = formDialog.getWindow().getAttributes();
		formDialogParams.width = LayoutParams.FILL_PARENT;
		formDialog.getWindow().setAttributes(
				(android.view.WindowManager.LayoutParams) formDialogParams);

		txtNama = (EditText) formDialog.findViewById(R.id.txtFormNamaPenulis);
		txtJudul = (EditText) formDialog.findViewById(R.id.txtFormJudulBuku);
		txtISBN = (EditText) formDialog.findViewById(R.id.txtFormISBN);

		btnBack = (Button) formDialog.findViewById(R.id.btnFormBatal);
		btnSimpan = (Button) formDialog.findViewById(R.id.btnFormSimpan);

		btnSimpan.setOnClickListener(new OnClickListener() {

//			@Override
			public void onClick(View view) {
				Book book = bookDB.get(txtISBN.getText().toString());
				if (book != null) {
					book.setJudul(txtJudul.getText().toString());
					book.setNamaPenulis(txtNama.getText().toString());
					bookDB.update(book);
				} else {
					book = new Book();
					book.setISBN(txtISBN.getText().toString());
					book.setJudul(txtJudul.getText().toString());
					book.setNamaPenulis(txtNama.getText().toString());

					bookDB.insert(book);
				}
				refreshListAdapter();
				formDialog.hide();
			}
		});

		btnBack.setOnClickListener(new OnClickListener() {

//			@Override
			public void onClick(View view) {

				formDialog.hide();
			}
		});
	}

	/**
	 * We need to refresh adapter for every data update. Without it, ListView will never be refreshed. 
	 */
	protected void refreshListAdapter() {
		listBook = bookDB.toArray();
		adapter.updateListBook(listBook);
		lsvBookView.setAdapter(adapter);
	}
}
