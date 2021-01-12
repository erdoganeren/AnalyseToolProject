function getHostname(){
    return window.location.origin; 
}

function getApiUrl(){
    //if (true)
        return "http://localhost:8181/api/issues/search?project=TestPoj&componentKeys=TestPoj";
    //return getHostname() + "/api/issues/search?project=TestPoj&componentKeys=TestPoj";
}

function onlaodBody(){
     //loadFromApi();
}

async function loadFromApi(){
    const resp = await fetch(getApiUrl());
    const jsonData = await resp.json();
    console.log("jsonData: ", jsonData);
    return jsonData;
}