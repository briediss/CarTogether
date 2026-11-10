const searchForm = document.getElementById('searchForm');
const tripResults = document.getElementById('tripResults');
const message = document.getElementById('message');
const resultsCount = document.getElementById('resultsCount');
const tenantSelect = document.getElementById('tenantSelect');
const departureTimeInput = document.getElementById('departureTime');
const bookingResults = document.getElementById("bookingResults");
const refreshBookings = document.getElementById("refreshBookings");

departureTimeInput.value = "2026-11-11T15:10";

searchForm.addEventListener('submit', function (event) {
    event.preventDefault();
    searchTrips();
});

tenantSelect.addEventListener("change", function () {
    tripResults.innerHTML = "";
    resultsCount.textContent = "0 found";
    message.textContent = "";
    loadBookings();
});

refreshBookings.addEventListener("click", loadBookings);

function showMessage(text, success) {

    message.textContent = text;
    message.className = success ? "message-success" : "message-error";
}

async function searchTrips() {

    message.textContent = "";
    message.className = "";
    tripResults.innerHTML = "";

    const departureCity = document.getElementById("departureCity").value;
    const arrivalCity = document.getElementById("arrivalCity").value;
    const departureTime = departureTimeInput.value;

    const params = new URLSearchParams({
        departureCity: departureCity,
        arrivalCity: arrivalCity,
        departureTime: departureTime
    });

    try {
        const response = await fetch("/api/trips/search?" + params, {
            headers: {"X-API-Key": tenantSelect.value}
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        const trips = await response.json();
        displayTrips(trips);

    } catch (error) {
        resultsCount.textContent = "0 found";
        showMessage(error.message, false);
    }
}

function displayTrips(trips) {
    tripResults.innerHTML = "";
    resultsCount.textContent = trips.length + " found";

    if (trips.length === 0) {
        tripResults.innerHTML =
            '<p class="empty-message">No rides found for your search.</p>';
        return;
    }

    trips.forEach(function (trip) {
        const card = document.createElement("div");
        card.className = "trip-card";

        const driver = document.createElement("div");
        driver.className = "driver-info";

        const avatar = document.createElement("div");
        avatar.className = "avatar";
        avatar.textContent = "👤";

        const driverText = document.createElement("div");

        const name = document.createElement("h3");
        name.textContent = trip.driverName;

        const rating = document.createElement("div");
        rating.className = "rating";
        rating.textContent = "⭐ " + trip.driverRating;

        driverText.append(name, rating);
        driver.append(avatar, driverText);

        const route = document.createElement("div");
        route.className = "trip-details";

        const from = document.createElement("p");
        from.textContent = "📍 " + trip.departureCity;

        const to = document.createElement("p");
        to.textContent = "📍 " + trip.arrivalCity;

        route.append(from, to);

        const dateInfo = document.createElement("div");
        dateInfo.className = "trip-details";

        const date = document.createElement("p");
        date.textContent = "📅 " + trip.departureTime.substring(0, 10);

        const time = document.createElement("p");
        time.textContent = "🕒 " + trip.departureTime.substring(11, 16);

        dateInfo.append(date, time);

        const seatsInfo = document.createElement("div");
        seatsInfo.className = "trip-details";

        const seats = document.createElement("p");
        seats.textContent = "👥 " + trip.availableSeats + " seats available";

        const detour = document.createElement("small");
        detour.textContent = "Detour: " + trip.detourMinutes + " min";

        seatsInfo.append(seats, detour);

        const actions = document.createElement("div");
        actions.className = "trip-actions";

        const price = document.createElement("div");
        price.className = "trip-price";
        price.textContent = trip.pricePerPerson.toFixed(2) + " €";

        const reserveButton = document.createElement("button");
        reserveButton.textContent = "Reserve";

        reserveButton.addEventListener("click", function () {
            bookTrip(trip.id, reserveButton);
        });

        actions.append(price, reserveButton);
        card.append(driver, route, dateInfo, seatsInfo, actions);
        tripResults.appendChild(card);
    });
}

async function bookTrip(tripId, button) {
    button.disabled = true;
    message.textContent = "";

    try {
        const response = await fetch("/api/bookings?tripId=" + tripId, {
            method: "POST",
            headers: {"X-API-Key": tenantSelect.value}
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        await searchTrips();
        await loadBookings();
        showMessage("Seat reserved successfully!", true);

    } catch (error) {
        showMessage(error.message, false);
    } finally {
        button.disabled = false;
    }
}

async function loadBookings() {
    bookingResults.replaceChildren();

    try {
        const response = await fetch("/api/bookings", {
            headers: {
                "X-API-Key": tenantSelect.value
            }
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        const bookings = await response.json();
        displayBookings(bookings);

    } catch (error) {
        showMessage(error.message, false);
    }
}

function displayBookings(bookings) {
    bookingResults.replaceChildren();

    if (bookings.length === 0) {
        const empty = document.createElement("p");
        empty.className = "empty-message";
        empty.textContent = "No bookings found.";
        bookingResults.appendChild(empty);
        return;
    }

    bookings.forEach(function (booking) {
        const card = document.createElement("div");
        card.className = "booking-card";

        const details = document.createElement("div");

        const title = document.createElement("h3");
        title.textContent = booking.trip.departureCity +
            " → " + booking.trip.arrivalCity;

        const driver = document.createElement("p");
        driver.textContent = "Driver: " + booking.trip.driverName;

        const date = document.createElement("p");
        date.textContent = "Departure: " +
            booking.trip.departureTime.replace("T", " ").substring(0, 16);

        const number = document.createElement("p");
        number.textContent = "Booking #" + booking.id;

        details.append(title, driver, date, number);

        const actions = document.createElement("div");
        actions.className = "booking-actions";

        const status = document.createElement("span");
        status.className = "booking-status";

        if (booking.cancelled) {
            status.textContent = "Cancelled";
            status.classList.add("status-cancelled");
        } else {
            status.textContent = "Active";
            status.classList.add("status-active");
        }

        actions.appendChild(status);

        if (!booking.cancelled) {
            const cancelButton = document.createElement("button");
            cancelButton.className = "cancel-button";
            cancelButton.textContent = "Cancel Booking";

            cancelButton.addEventListener("click", function () {
                cancelBooking(booking.id, cancelButton);
            });

            actions.appendChild(cancelButton);
        }

        card.append(details, actions);
        bookingResults.appendChild(card);
    });
}

async function cancelBooking(bookingId, button) {
    if (!confirm("Are you sure you want to cancel this booking?")) {
        return;
    }

    button.disabled = true;

    try {
        const response = await fetch(
            "/api/bookings/" + bookingId + "/cancel", {
                method: "PATCH",
                headers: {
                    "X-API-Key": tenantSelect.value
                }
            }
        );

        if (!response.ok) {
            throw new Error(await response.text());
        }

        await loadBookings();
        await searchTrips();

        showMessage("Booking cancelled successfully!", true);

    } catch (error) {
        showMessage(error.message, false);
    } finally {
        button.disabled = false;
    }
}

loadBookings();