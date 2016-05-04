print("hello World");

var claim = execution.getVariable("claim");

var claims = execution.getVariable("claims");

var claimResult;

print(claims);

print(claim.IMEI);

print(claim.claimActions);

var result;

print(claim.IMEI.length());
if (claim.IMEI.length() == 15 || claim.IMEI.length() == 18) {
  result = true;
  claimResult = "IMIE ok";
} else {
	claimResult = "IMEI nok";
	result = false;
	}	

execution.setVariable("claimResult", claimResult);

print(result);

result;