package ohm.softa.a07.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ohm.softa.a07.api.OpenMensaAPI;
import ohm.softa.a07.model.Meal;
import ohm.softa.a07.utils.MealsFilterUtility;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable {

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

	// use annotation to tie to component in XML
	@FXML
	private Button btnRefresh;

	@FXML
	private ListView<Meal> mealsList;

	//@FXML
	//private ListView<Meal> listView;
	private ObservableList<Meal> meals;
	private final OpenMensaAPI openMensaAPI;

	@FXML
	private Button btnClose;

	@FXML
	private CheckBox chkVegetarian;

	public MainController() {
		// setup logging
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

		Retrofit retrofit = new Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.client(client)
			.baseUrl("https://openmensa.org/")
			.build();

		openMensaAPI = retrofit.create(OpenMensaAPI.class);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		meals = mealsList.getItems();

		// set the event handler (callback)
		btnRefresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// create a new (observable) list and tie it to the view
				// ObservableList<String> list = FXCollections.observableArrayList("Hans", "Dampf");
				ObservableList<String> list = FXCollections.observableArrayList("Hans", "Dampf");

				openMensaAPI.getMeals(dateFormat.format(new Date())).enqueue(new Callback<List<Meal>>() {

					@Override
					public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
						if (!response.isSuccessful())
							return;
						if (response.body() == null) {
							return;
						}


						Platform.runLater(() -> {
							meals.clear();
							meals.addAll(chkVegetarian.isSelected()
								? MealsFilterUtility.filterForVegetarian(response.body())
								: response.body());
						});
					}

					@Override
					public void onFailure(Call<List<Meal>> call, Throwable t) {
						/* Show an alert if loading of mealsProperty fails */
						Platform.runLater(() -> {
							meals.clear();
							new Alert(Alert.AlertType.ERROR, "Failed to get mealsProperty", ButtonType.OK).showAndWait();
						});
					}
				});

			}
		});
		btnClose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//closing main window
				Platform.exit();
				//terminate process after closing main window
				System.exit(0);
			}
		});
	}
}
/*
package ohm.softa.a07.controllers;

	import javafx.collections.FXCollections;
	import javafx.collections.ListChangeListener;
	import javafx.collections.ObservableList;
	import javafx.event.ActionEvent;
	import javafx.event.EventHandler;
	import javafx.fxml.FXML;
	import javafx.fxml.Initializable;
	import javafx.scene.control.Button;
	import javafx.scene.control.CheckBox;
	import javafx.scene.control.ListView;
	import ohm.softa.a07.model.Meal;

	import java.net.URL;
	import java.util.ResourceBundle;
public class MainController implements Initializable {

	// use annotation to tie to component in XML
	@FXML
	private Button btnRefresh;

	@FXML
	private ListView<String> mealsList;

	@FXML
	private ListView<Meal> listView;
	private ObservableList<Meal> observableList;

	@FXML
	private Button close;

	@FXML
	private CheckBox vegetarian;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// set the event handler (callback)
		btnRefresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// create a new (observable) list and tie it to the view
				ObservableList<String> list = FXCollections.observableArrayList("Hans", "Dampf");
				mealsList.setItems(list);
			}
		});
		close.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

			}
		});
		vegetarian.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

			}
		});
	}
}
*/
