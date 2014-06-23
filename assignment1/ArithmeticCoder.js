// Tui Popenoe
// JS Arithmetic Coder

// Character probabilities
// Character : [start, width]
var prob = {};

// Stores the characters present in the string
var char1;
var char2;
var char3;
var char4;
var char5;

// Store the starting values
var start1;
var start2;
var start3;
var start4;
var start5;

// Stores the widths of each character on the table;
var width1;
var width2;
var width3;
var width4;
var width5;

// Stores the input string
var input;
// Stores the html to be output
var output;

// Ensure the initial input is loaded
window.onload = function(){
    updateInput();
};

function compress(){
    console.log('Compress()' + input);

    // Clear the html in the display
    $('#output').empty();

    updateInput();

    validateInput();

    computeProbability();

    setProbabilityTable();

    // Output the input string
    output = '<p>Compress('+ input + ')</div>';
    append(output, 1);

    // Get the encoded version of the input, using the input string
    // and the probability table
    var encoded_input = encode(input, prob);

    // Decoded the binary version of the original input string using the
    // probability table
    decode(encoded_input, prob);
};

function append(html, time){
    $(output).hide().appendTo('#output').slideUp(300).delay(1000 * time)
        .fadeIn(1000);
};

function updateInput(){
    console.log('Updated Input: ' + input);
    // Get the input from the input box
    input = document.getElementById('stringInput').value;
    // Append the end marker to the string
    input += char5;
    // Get the chars in the string
    char1 = document.getElementById('char1').value;
    char2 = document.getElementById('char2').value;
    char3 = document.getElementById('char3').value;
    char4 = document.getElementById('char4').value;
    char5 = document.getElementById('char5').value;
};

function setProbabilityTable(){
    validateInput();
    // Set the probability table to the new values
    prob = {};
    prob[char1] = [start1, width1];
    prob[char2] = [start2, width2],
    prob[char3] = [start3, width3];
    prob[char4] = [start4, width4];
    prob[char5] = [start5, width5];
};

function computeProbability(){
    validateInput();

    // Set this way so counts don't become global
    var oneCount, twoCount, threeCount, fourCount;
    oneCount = twoCount = threeCount = fourCount = 0;

    for(var i = 0; i < input.length; i++){
        switch(input.charAt(i)){
            case char1:
                oneCount++;
                break;
            case char2:
                twoCount++;
                break;
            case char3:
                threeCount++;
                break;
            case char4:
                fourCount++;
                break;
            default:
                break;
        }
    }

    width1 = oneCount / input.length;
    width2 = twoCount / input.length;
    width3 = threeCount / input.length;
    width4 = fourCount / input.length;
    width5 = 1 / input.length;

    start1 = 0;
    start2 = width1;
    start3 = width1 + width2;
    start4 = width1 + width2 + width3;
    start5 = width1 + width2 + width3 + width4;

    updateTableInfo();
};

function updateTableInfo(){
    document.getElementById('width1').innerHTML = width1;
    document.getElementById('width2').innerHTML = width2;
    document.getElementById('width3').innerHTML = width3;
    document.getElementById('width4').innerHTML = width4;
    document.getElementById('width5').innerHTML = width5;

    document.getElementById('start1').innerHTML = start1;
    document.getElementById('start2').innerHTML = start2;
    document.getElementById('start3').innerHTML = start3;
    document.getElementById('start4').innerHTML = start4;
    document.getElementById('start5').innerHTML = start5;
};

// Validate input in the input string and probability table
function validateInput(){

    if(char1 == null || char1 == NaN || char1 == undefined || char1.length >1
        || char1.length == 0){
        char1 = prompt("Enter a valid character for character1: ", "");
    }
    if(char2 == null || char2 == NaN || char2 == undefined || char2.length >1
        || char2.length == 0){
        char2 = prompt("Enter a valid character for character2: ", "");
    }
    if(char3 == null || char3 == NaN || char3 == undefined || char3.length >1
        || char3.length == 0){
        char3 = prompt("Enter a valid character for character3: ", "");
    }
    if(char4 == null || char4 == NaN || char4 == undefined || char4.length >1
        || char4.length == 0){
        char4 = prompt("Enter a valid character for character4: ", "");
    }
    if(char5 == null || char5 == NaN || char5 == undefined || char5.length >1
        || char5.length == 0){
        char5 = prompt("Enter a valid character for character5: ", "");
    }

    for(var i in input){
        if(input.charAt(i) == char1){

        }
        else if(input.charAt(i) == char2){

        }
        else if(input.charAt(i) == char3){

        }
        else if(input.charAt(i) == char4){

        }
        else if(input.charAt(i) == char5){

        }
        else{
            alert('Invalid character detected in input, character will be ignored');
        }
    }
}

// Encode the string according to the probability table
function encode(str, prob){
    console.log('Encode String: ' + str);
    output = '<p>Encode String: ' + str + '</p>';
    append(output, 2);

    var start = 0;
    var width = 1;
    for(var i = 0; i < str.length; i++){
        console.log(str.charAt(i));

        console.log(prob);
        ch = str.charAt(i);
        console.log(ch);

        var d_start = prob[ch][0];
        var d_width = prob[ch][1];

        start += d_start*width;
        console.log(start);
        output = '<p> Character at ' + i +': ' + str.charAt(i) +
            '</p><p>Start: ' + start + '</p>';
        append(output, 3);
        width *= d_width;
        output = '<p> Interval Width: ' + width + '</p>';
        append(output, 3);
    }

    var encoded_value = getRandomUniform(start, start + width);
    console.log(encoded_value);
    output = '<p> Encoded value: ' + encoded_value + '</p>';
    append(output, 4);
    console.log(floatToBinary(encoded_value));
    output = '<p>Binary Representation: </p><p>'
        + floatToBinary(encoded_value) + '</p>';
    append(output, 5);

    console.log('Encoded Value: ' + encoded_value);
    return encoded_value;
}

// Return a random number between min and max
function getRandomUniform(min, max){
    return Math.random() * (max - min) + min;
}

// Return the binary value of the float
function floatToBinary(value){
    if(value >= 0) {
        return value.toString(2);
    }
    else {
        return (~value).toString(2);
    }
}

// Decode the float using the probability table
function decode(num, prob){
    console.log('Decode Number: ');
    console.log(num);
    output = '<p>Decode Number: ' + num + '</p>';
    append(output, 6);
    var decoded = '';
    var start;
    var width;
    while(true){
        for(var key in prob){
            start = prob[key][0];
            console.log('start: ' + start);
            width = prob[key][1];
            console.log('width: ' + width);
            if((0 <= (num - start)) && ((num -start) < width)){
                num = (num - start) / width;
                output = '<p> Encoded Remainder: ' + num + '</p>';
                append(output, 7);
                console.log(num);
                decoded += (key);
                console.log(key);
                console.log('Decoded string: ' + decoded);
                output = '<p>Decoded string: ' + decoded + '</p>';
                append(output, 7);
                break;
            }
        }
        if(key == '@'){
            break;
        }
    }

    decoded = decoded.substring(0, decoded.length-1);
    console.log(decoded);
    console.log('Decoded string: ');
    output = '<p>Final String: ' + decoded + '</p>';
    append(output, 9);
    console.log(decoded);
    return decoded;
}