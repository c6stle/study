
function base64url(source) {
  encodedSource = CryptoJS.enc.Base64.stringify(source);
  encodedSource = encodedSource.replace(/=+$/, '');
  encodedSource = encodedSource.replace(/\+/g, '-');
  encodedSource = encodedSource.replace(/\//g, '_');
  
  return encodedSource;
}
	
function fnGenerateToken(data){
	var header = {
		"alg": "HS512",
	};
	var signingKey = "SIGNINGKEY-SANGS2022";
	var resultToken = "";
	
	var stringHeader = CryptoJS.enc.Utf8.parse(JSON.stringify(header));
	var encodedHeader = base64url(stringHeader);
	var stringData = CryptoJS.enc.Utf8.parse(JSON.stringify(data));
	var encodedData = base64url(stringData);
	var signature = encodedHeader + "." + encodedData;
	signature = CryptoJS.HmacSHA512(signature, signingKey);
	signature = base64url(signature);
	
	resultToken = encodedHeader+"."+encodedData+"."+signature;
	
	return resultToken;
}
