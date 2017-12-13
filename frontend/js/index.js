
console.log("inside js");
$(document).ready(function(){
    var patients;
    $.ajax({
        url: 'http://localhost:8080/rest/system/retrieveAll',
        method: 'GET',
        contentType: 'application/json', //type of data recieved from server.
        success: function(res){ 
           console.log(res);
           patients = res;
        },
        error: function(err){
            console.log(err);
        }
    });
    setTimeout(function(){
        console.log(patients)
        // preserve newlines, etc - use valid JSON
        patients = patients.replace(/\\n/g, "\\n")  
        .replace(/\\'/g, "\\'")
        .replace(/\\"/g, '\\"')
        .replace(/\\&/g, "\\&")
        .replace(/\\r/g, "\\r")
        .replace(/\\t/g, "\\t")
        .replace(/\\b/g, "\\b")
        .replace(/\\f/g, "\\f");
        // remove non-printable and other non-valid JSON chars
        patients = patients.replace(/[\u0000-\u0019]+/g,""); 
        var listitems = '';
        JSON.parse(patients).forEach(function(p){
            var string = p.firstName + " " + p.lastName;
            listitems += '<option style={width: 100px; min-width: 50px; max-width: 50px;} value=' + p.id + '>' + string + '</option>';
        });
        $("#select").append(listitems);
        var id;
        $('#select').change(function(){ 
            id = $(this).val();
            var patient;
            JSON.parse(patients).forEach(function(p){
                if(p.id==id){
                    patient = p;
                }
            })
            var firstName = document.getElementById('firstName');
            firstName.value = patient.firstName;
            var lastName = document.getElementById('lastName');
            lastName.value = patient.lastName;
            var address = document.getElementById('address');
            address.value = patient.address;
            var birthDate = document.getElementById('birthDate');
            birthDate.value = patient.birthDate;
            var status = document.getElementById('status');
            status.value = patient.status;
            var phoneNumber = document.getElementById('phoneNumber');
            phoneNumber.value = patient.phoneNumber;
        });
        $("#update").click(function(){
            var firstName = document.getElementById('firstName').value;
            var lastName = document.getElementById('lastName').value;
            var address = document.getElementById('address').value;
            var birthDate = document.getElementById('birthDate').value;
            var status = document.getElementById('status').value;
            var phoneNumber = document.getElementById('phoneNumber').value;
            jsonObject={
                "firstName":firstName,
                "lastName":lastName,
                "address":address,
                "phoneNumber":phoneNumber,
                "status":status,
                "birthDate":birthDate
            }
            $.ajax({
                url: 'http://localhost:8080/rest/system/updatePatient/'+id,
                method: 'PUT',
                data: JSON.stringify(jsonObject),
                dataType: 'json',
                contentType: "application/json; charset=utf-8",
                success: function(res){ 
                    console.log(res);
                    window.alert("patient successfully updated!");
                },
                error: function(err){
                    console.log(err);
                }
            })
        })
    },5000);
    $('#viewAll').on('click',function(){
        console.log("View All Button Pressed");
        $.ajax({
            url: 'http://localhost:8080/rest/system/retrieveAll',
            method: 'GET',
            contentType: 'application/json', //type of data recieved from server.
            success: function(res){ 
                patientArray = JSON.parse(res);
                console.log(patientArray);
                var html = ''
                patientArray.forEach(function(p){
                    var element = "<li><div class='user-avatar'><a href='#'>"+
                    "</div><p class='user-name'><a>" +p.firstName + " "+p.lastName+
                    "</a><span>" +p.address +"</span>"+
                    "</a><span>" +p.phoneNumber +"</span>"+
                    "</a><span>" +p.birthDate +"</span>"+
                    "</a><span>" +p.status +"</span></p></li>"
                    html+=element;
                })
                $(".user-profiles-list-minimal").append(html);
            },
            error: function(err){
                console.log(err);
            }
        });
    });
    $("#create").click(function(){
        alert("Clicked");
        var firstName = document.getElementById('firstName').value;
        var lastName = document.getElementById('lastName').value;
        var address = document.getElementById('address').value;
        var birthDate = document.getElementById('birthDate').value;
        var status = document.getElementById('status').value;
        var phoneNumber = document.getElementById('phoneNumber').value;
        jsonObject={
            "firstName":firstName,
            "lastName":lastName,
            "address":address,
            "phoneNumber":phoneNumber,
            "status":status,
            "birthDate":birthDate
        }
        $.ajax({
            url: 'http://localhost:8080/rest/system/addPatient/',
            method: 'POST',
            data: JSON.stringify(jsonObject),
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            success: function(res){ 
                console.log(res);
                alert("patient successfully created!");
            },
            error: function(err){
                console.log(err);
            }
        })
    })
});