
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
            console.log("update clicked");
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
                contentType: "application/json; charset=utf-8",
                success: function(res){ 
                    console.log(res.responseText);
                    alert("Patient Successfully Updated");
                    // alert(res.responseText);
                },
                error: function(err){
                    console.log(err.responseText);
                    alert(err.responseText);
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
                var patientArray = JSON.parse(res);
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
        console.log("Create Clicked");
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
                console.log(res.responseText);
                alert(res.responseText);
            },
            error: function(err){
                console.log(err);
                alert(err.responseText);
            }
        })
    })
    $("#firstName").attr('required',true);
    $("#lastName").attr('required',true);
    $("#address").attr('required',true);
    $("#status").attr('required',true);
    $("#phoneNumber").attr('required',true);
    $("#birthDate").attr('required',true);
    $('#viewAll').click();
});