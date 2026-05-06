#!/usr/bin/env bash
# Comprehensive endpoint smoke test for VMS

BASE=http://localhost:8080
PASS=0
FAIL=0
FAILED_TESTS=()

check() {
  local name="$1" expected="$2" actual="$3"
  if [[ "$actual" == "$expected" ]]; then
    PASS=$((PASS + 1))
    printf "  \e[32m✓\e[0m %-55s [%s]\n" "$name" "$actual"
  else
    FAIL=$((FAIL + 1))
    FAILED_TESTS+=("$name (expected $expected got $actual)")
    printf "  \e[31m✗\e[0m %-55s [expected %s got %s]\n" "$name" "$expected" "$actual"
  fi
}

extract_id() { grep -oE '"id":"[^"]+"' | head -1 | sed 's/.*"id":"//;s/"//'; }

req_status() { curl -s -o /dev/null -w "%{http_code}" "$@"; }
req_body() { curl -s "$@"; }

echo
echo "========== USER MANAGEMENT =========="
USER_RES=$(req_body -X POST $BASE/api/users -H "Content-Type: application/json" \
  -d '{"name":"Admin User","email":"admin@vms.lk","phone":"0771111111","address":"HQ","role":"ADMIN","password":"admin"}')
USER_ID=$(echo "$USER_RES" | extract_id)
check "POST /api/users (admin)"     "$USER_ID" "$([[ -n "$USER_ID" ]] && echo "$USER_ID" || echo "FAIL")"

USER2_RES=$(req_body -X POST $BASE/api/users -H "Content-Type: application/json" \
  -d '{"name":"Customer One","email":"cust1@vms.lk","phone":"0712222222","address":"Galle","role":"CUSTOMER","password":"pass"}')
USER2_ID=$(echo "$USER2_RES" | extract_id)
check "POST /api/users (customer)"  "$USER2_ID" "$([[ -n "$USER2_ID" ]] && echo "$USER2_ID" || echo "FAIL")"

check "POST /api/users (empty body 400)"     "400" "$(req_status -X POST $BASE/api/users -H 'Content-Type: application/json' -d '{}')"
check "GET  /api/users"                       "200" "$(req_status $BASE/api/users)"
check "GET  /api/users?q=admin"               "200" "$(req_status $BASE/api/users?q=admin)"
check "GET  /api/users/{id}"                  "200" "$(req_status $BASE/api/users/$USER_ID)"
check "GET  /api/users/missing (404)"         "404" "$(req_status $BASE/api/users/nope)"
check "PUT  /api/users/{id}"                  "200" "$(req_status -X PUT $BASE/api/users/$USER2_ID -H 'Content-Type: application/json' -d '{"name":"Customer One Updated","email":"cust1@vms.lk","phone":"0712222222","address":"Matara","role":"CUSTOMER","password":"newpass"}')"
check "PUT  /api/users/missing (404)"         "404" "$(req_status -X PUT $BASE/api/users/nope -H 'Content-Type: application/json' -d '{"name":"x","email":"x@x"}')"

USER3_RES=$(req_body -X POST $BASE/api/users -H "Content-Type: application/json" \
  -d '{"name":"To Delete","email":"del@vms.lk","role":"CUSTOMER","password":"x"}')
USER3_ID=$(echo "$USER3_RES" | extract_id)
check "DELETE /api/users/{id}"                "200" "$(req_status -X DELETE $BASE/api/users/$USER3_ID)"
check "DELETE /api/users/missing (404)"       "404" "$(req_status -X DELETE $BASE/api/users/nope)"

echo
echo "========== VEHICLE MANAGEMENT =========="
V_RES=$(req_body -X POST $BASE/api/vehicles -H "Content-Type: application/json" \
  -d "{\"plateNumber\":\"CAB-1234\",\"ownerId\":\"$USER2_ID\",\"brand\":\"Toyota\",\"model\":\"Aqua\",\"type\":\"CAR\",\"color\":\"White\",\"year\":2020}")
V_ID=$(echo "$V_RES" | extract_id)
check "POST /api/vehicles"                    "$V_ID" "$([[ -n "$V_ID" ]] && echo "$V_ID" || echo FAIL)"
check "POST /api/vehicles (no plate 400)"     "400" "$(req_status -X POST $BASE/api/vehicles -H 'Content-Type: application/json' -d '{"brand":"x"}')"
check "GET  /api/vehicles"                    "200" "$(req_status $BASE/api/vehicles)"
check "GET  /api/vehicles?q=toyota"           "200" "$(req_status $BASE/api/vehicles?q=toyota)"
check "GET  /api/vehicles?ownerId=..."        "200" "$(req_status $BASE/api/vehicles?ownerId=$USER2_ID)"
check "GET  /api/vehicles/{id}"               "200" "$(req_status $BASE/api/vehicles/$V_ID)"
check "GET  /api/vehicles/missing (404)"      "404" "$(req_status $BASE/api/vehicles/nope)"
check "PUT  /api/vehicles/{id}"               "200" "$(req_status -X PUT $BASE/api/vehicles/$V_ID -H 'Content-Type: application/json' -d "{\"plateNumber\":\"CAB-1234\",\"ownerId\":\"$USER2_ID\",\"brand\":\"Toyota\",\"model\":\"Aqua\",\"type\":\"CAR\",\"color\":\"Black\",\"year\":2021}")"

V2_RES=$(req_body -X POST $BASE/api/vehicles -H "Content-Type: application/json" \
  -d "{\"plateNumber\":\"BIKE-77\",\"ownerId\":\"$USER2_ID\",\"brand\":\"Honda\",\"type\":\"BIKE\",\"year\":2022}")
V2_ID=$(echo "$V2_RES" | extract_id)
check "DELETE /api/vehicles/{id}"             "200" "$(req_status -X DELETE $BASE/api/vehicles/$V2_ID)"

echo
echo "========== PARKING SLOT MANAGEMENT =========="
S_RES=$(req_body -X POST $BASE/api/slots -H "Content-Type: application/json" \
  -d '{"slotNumber":"A-101","location":"Floor 1","type":"CAR","hourlyRate":200,"status":"AVAILABLE"}')
S_ID=$(echo "$S_RES" | extract_id)
S2_RES=$(req_body -X POST $BASE/api/slots -H "Content-Type: application/json" \
  -d '{"slotNumber":"A-102","location":"Floor 1","type":"CAR","hourlyRate":200,"status":"AVAILABLE"}')
S2_ID=$(echo "$S2_RES" | extract_id)

check "POST /api/slots"                       "$S_ID" "$([[ -n "$S_ID" ]] && echo "$S_ID" || echo FAIL)"
check "POST /api/slots (no number 400)"       "400" "$(req_status -X POST $BASE/api/slots -H 'Content-Type: application/json' -d '{}')"
check "GET  /api/slots"                       "200" "$(req_status $BASE/api/slots)"
check "GET  /api/slots?q=floor"               "200" "$(req_status $BASE/api/slots?q=floor)"
check "GET  /api/slots?available=true"        "200" "$(req_status $BASE/api/slots?available=true)"
check "GET  /api/slots/{id}"                  "200" "$(req_status $BASE/api/slots/$S_ID)"
check "GET  /api/slots/missing (404)"         "404" "$(req_status $BASE/api/slots/nope)"
check "PUT  /api/slots/{id}"                  "200" "$(req_status -X PUT $BASE/api/slots/$S_ID -H 'Content-Type: application/json' -d '{"slotNumber":"A-101","location":"Floor 1","type":"CAR","hourlyRate":250,"status":"AVAILABLE"}')"

S3_RES=$(req_body -X POST $BASE/api/slots -H "Content-Type: application/json" \
  -d '{"slotNumber":"DEL-1","type":"CAR","hourlyRate":100}')
S3_ID=$(echo "$S3_RES" | extract_id)
check "DELETE /api/slots/{id}"                "200" "$(req_status -X DELETE $BASE/api/slots/$S3_ID)"

echo
echo "========== BOOKING MANAGEMENT =========="
B_RES=$(req_body -X POST $BASE/api/bookings -H "Content-Type: application/json" \
  -d "{\"userId\":\"$USER2_ID\",\"vehicleId\":\"$V_ID\",\"slotId\":\"$S_ID\",\"startTime\":\"2026-05-05T10:00:00\",\"endTime\":\"2026-05-05T13:00:00\",\"totalAmount\":0,\"status\":\"CONFIRMED\"}")
B_ID=$(echo "$B_RES" | extract_id)

check "POST /api/bookings"                    "$B_ID" "$([[ -n "$B_ID" ]] && echo "$B_ID" || echo FAIL)"
check "POST /api/bookings (missing fields 400)" "400" "$(req_status -X POST $BASE/api/bookings -H 'Content-Type: application/json' -d '{}')"
check "GET  /api/bookings"                    "200" "$(req_status $BASE/api/bookings)"
check "GET  /api/bookings?status=CONFIRMED"   "200" "$(req_status $BASE/api/bookings?status=CONFIRMED)"
check "GET  /api/bookings?userId=..."         "200" "$(req_status $BASE/api/bookings?userId=$USER2_ID)"
check "GET  /api/bookings/{id}"               "200" "$(req_status $BASE/api/bookings/$B_ID)"
check "GET  /api/bookings/missing (404)"      "404" "$(req_status $BASE/api/bookings/nope)"

# verify slot was marked OCCUPIED
SLOT_STATUS=$(req_body $BASE/api/slots/$S_ID | grep -oE '"status":"[^"]+"' | sed 's/.*"status":"//;s/"//')
check "Side effect: slot OCCUPIED after book" "OCCUPIED" "$SLOT_STATUS"

# verify auto payment was created
PAYMENT_COUNT=$(req_body "$BASE/api/payments?bookingId=$B_ID" | grep -oE '"id":"[^"]+"' | wc -l | tr -d ' ')
check "Side effect: PENDING payment created"  "1" "$PAYMENT_COUNT"

check "PUT  /api/bookings/{id} -> CANCELLED"  "200" "$(req_status -X PUT $BASE/api/bookings/$B_ID -H 'Content-Type: application/json' -d "{\"userId\":\"$USER2_ID\",\"vehicleId\":\"$V_ID\",\"slotId\":\"$S_ID\",\"startTime\":\"2026-05-05T10:00:00\",\"endTime\":\"2026-05-05T13:00:00\",\"totalAmount\":600,\"status\":\"CANCELLED\"}")"

# verify slot freed after cancel
SLOT_STATUS_2=$(req_body $BASE/api/slots/$S_ID | grep -oE '"status":"[^"]+"' | sed 's/.*"status":"//;s/"//')
check "Side effect: slot AVAILABLE after cancel" "AVAILABLE" "$SLOT_STATUS_2"

# create a booking to delete
B2_RES=$(req_body -X POST $BASE/api/bookings -H "Content-Type: application/json" \
  -d "{\"userId\":\"$USER2_ID\",\"vehicleId\":\"$V_ID\",\"slotId\":\"$S2_ID\",\"startTime\":\"2026-05-05T20:00:00\",\"endTime\":\"2026-05-05T21:00:00\",\"totalAmount\":0}")
B2_ID=$(echo "$B2_RES" | extract_id)
check "DELETE /api/bookings/{id}"             "200" "$(req_status -X DELETE $BASE/api/bookings/$B2_ID)"

echo
echo "========== PAYMENT MANAGEMENT =========="
P_RES=$(req_body -X POST $BASE/api/payments -H "Content-Type: application/json" \
  -d "{\"bookingId\":\"$B_ID\",\"userId\":\"$USER2_ID\",\"amount\":600,\"method\":\"CARD\",\"transactionId\":\"VISA-9999\",\"status\":\"PAID\"}")
P_ID=$(echo "$P_RES" | extract_id)
check "POST /api/payments"                    "$P_ID" "$([[ -n "$P_ID" ]] && echo "$P_ID" || echo FAIL)"
check "POST /api/payments (no booking 400)"   "400" "$(req_status -X POST $BASE/api/payments -H 'Content-Type: application/json' -d '{"amount":100}')"
check "GET  /api/payments"                    "200" "$(req_status $BASE/api/payments)"
check "GET  /api/payments?status=PAID"        "200" "$(req_status $BASE/api/payments?status=PAID)"
check "GET  /api/payments?bookingId=..."      "200" "$(req_status $BASE/api/payments?bookingId=$B_ID)"
check "GET  /api/payments?userId=..."         "200" "$(req_status $BASE/api/payments?userId=$USER2_ID)"
check "GET  /api/payments?q=visa"             "200" "$(req_status $BASE/api/payments?q=visa)"
check "GET  /api/payments/{id}"               "200" "$(req_status $BASE/api/payments/$P_ID)"
check "GET  /api/payments/missing (404)"      "404" "$(req_status $BASE/api/payments/nope)"
check "PUT  /api/payments/{id}"               "200" "$(req_status -X PUT $BASE/api/payments/$P_ID -H 'Content-Type: application/json' -d "{\"bookingId\":\"$B_ID\",\"userId\":\"$USER2_ID\",\"amount\":600,\"method\":\"CARD\",\"transactionId\":\"VISA-9999\",\"status\":\"REFUNDED\"}")"
check "DELETE /api/payments/{id}"             "200" "$(req_status -X DELETE $BASE/api/payments/$P_ID)"

echo
echo "========== FEEDBACK MANAGEMENT =========="
F_RES=$(req_body -X POST $BASE/api/feedback -H "Content-Type: application/json" \
  -d "{\"userId\":\"$USER2_ID\",\"subject\":\"Great service\",\"message\":\"Loved the parking experience\",\"rating\":5,\"status\":\"OPEN\"}")
F_ID=$(echo "$F_RES" | extract_id)
check "POST /api/feedback"                    "$F_ID" "$([[ -n "$F_ID" ]] && echo "$F_ID" || echo FAIL)"
check "POST /api/feedback (no subject 400)"   "400" "$(req_status -X POST $BASE/api/feedback -H 'Content-Type: application/json' -d '{}')"
check "GET  /api/feedback"                    "200" "$(req_status $BASE/api/feedback)"
check "GET  /api/feedback?q=great"            "200" "$(req_status $BASE/api/feedback?q=great)"
check "GET  /api/feedback?userId=..."         "200" "$(req_status $BASE/api/feedback?userId=$USER2_ID)"
check "GET  /api/feedback/{id}"               "200" "$(req_status $BASE/api/feedback/$F_ID)"
check "GET  /api/feedback/missing (404)"      "404" "$(req_status $BASE/api/feedback/nope)"
check "PUT  /api/feedback/{id}"               "200" "$(req_status -X PUT $BASE/api/feedback/$F_ID -H 'Content-Type: application/json' -d "{\"userId\":\"$USER2_ID\",\"subject\":\"Great service\",\"message\":\"Loved the parking experience\",\"rating\":5,\"status\":\"RESOLVED\"}")"

F2_RES=$(req_body -X POST $BASE/api/feedback -H "Content-Type: application/json" \
  -d "{\"userId\":\"$USER2_ID\",\"subject\":\"Issue\",\"message\":\"Slot was dirty\",\"rating\":2}")
F2_ID=$(echo "$F2_RES" | extract_id)
check "DELETE /api/feedback/{id}"             "200" "$(req_status -X DELETE $BASE/api/feedback/$F2_ID)"

echo
echo "========== REPORTS =========="
check "GET /api/reports/dashboard"            "200" "$(req_status $BASE/api/reports/dashboard)"
check "GET /api/reports/history"              "200" "$(req_status $BASE/api/reports/history)"
check "GET /api/reports/revenue"              "200" "$(req_status $BASE/api/reports/revenue)"
check "GET /api/reports/bookings-by-status"   "200" "$(req_status $BASE/api/reports/bookings-by-status)"
check "GET /api/reports/vehicles-by-type"     "200" "$(req_status $BASE/api/reports/vehicles-by-type)"
check "GET /api/reports/payments-by-method"   "200" "$(req_status $BASE/api/reports/payments-by-method)"

echo
echo "========== STATIC PAGES =========="
for page in / /index.html /users.html /vehicles.html /slots.html /bookings.html /payments.html /feedback.html /reports.html /css/style.css /js/api.js; do
  check "GET $page" "200" "$(req_status $BASE$page)"
done

echo
echo "===================================================="
TOTAL=$((PASS + FAIL))
printf "  TOTAL: %d  |  PASS: \e[32m%d\e[0m  |  FAIL: \e[31m%d\e[0m\n" "$TOTAL" "$PASS" "$FAIL"
echo "===================================================="
if (( FAIL > 0 )); then
  echo "Failed tests:"
  for t in "${FAILED_TESTS[@]}"; do echo "  - $t"; done
  exit 1
fi
