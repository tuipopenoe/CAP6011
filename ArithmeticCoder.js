// Tui Popenoe
// JS Arithmetic Coder

// Arbitrary Character Probabilities
// TODO: Use an input form to change
prob = {
    'a' : [0.0, 0.3],
    'b' : [0.3, 0.2],
    'c' : [0.5, 0.2],
    'd' : [0.7, 0.2],
    '@' : [0.9, 0.1]
}

var input;
var outputHTML;

function compress(){
    $('#output').empty();
    console.log('Compress()' + input);
    outputHTML = '<p>Compress('+ input + ')</div>';
    append(outputHTML, 1);

    var encoded_input = encode(input, prob);

    decode(encoded_input, prob);

}

function append(html, time){
    $(outputHTML).hide().appendTo('#output').slideUp(300).delay(1000 * time)
        .fadeIn(1000);
}

function updateInput(){
    input = document.getElementById('stringInput').value;
    input += '@';
    console.log('Updated Input: ' + input);
}


function encode(str, prob){
    console.log('Encode String: ' + str);
    outputHTML = '<p>Encode String: ' + str + '</p>';
    append(outputHTML, 2);
    console.log('Character Probabilities: ' + prob);

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
        outputHTML = '<p>' + str.charAt(i) + '</p><p>Start: ' + start + '</p>';
        append(outputHTML, 3);
        width *= d_width;
    }

    var encoded_value = getRandomUniform(start, start + width);
    console.log(encoded_value);
    outputHTML = '<p> Encoded value: ' + encoded_value + '</p>';
    append(outputHTML, 4);
    console.log(floatToBinary(encoded_value));
    outputHTML = '<p>Binary Representation: </p><p>'
        + floatToBinary(encoded_value) + '</p>';
    append(outputHTML, 5);

    console.log('Encoded Value: ' + encoded_value);
    return encoded_value;
}

// Return a random number between min and max
function getRandomUniform(min, max){
    return Math.random() * (max - min) + min;
}

function floatToBinary(value){
    if(value >= 0) {
        return value.toString(2);
    }
    else {
        return (~value).toString(2);
    }
}

function decode(num, prob){
    console.log('Decode Number: ');
    console.log(num);
    outputHTML = '<p>Decode Number: ' + num + '</p>';
    append(outputHTML, 6);
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
                outputHTML = '<p> Encoded Remainder: ' + num + '</p>';
                append(outputHTML, 7);
                console.log(num);
                decoded += (key);
                console.log(key);
                console.log('Decoded string: ' + decoded);
                outputHTML = '<p>Decoded string: ' + decoded + '</p>';
                append(outputHTML, 7);
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
    outputHTML = '<p>Final String: ' + decoded + '</p>';
    append(outputHTML, 9);
    console.log(decoded);
    return decoded;
}