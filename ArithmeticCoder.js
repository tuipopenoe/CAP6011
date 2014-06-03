// Tui Popenoe
// JS Arithmetic Coder

// Arbitrary Character Probabilities
// TODO: Use an input form to change
prob = {
    'a' : [0.0, 0.4],
    'b' : [0.4, 0.3],
    'c' : [0.7, 0.2],
    '@' : [0.9, 0.1]
}

var input;

function compress(){
    encode(input, prob);
    console.log('Encode Input: ' + input);
}

function updateInput(){
    input = document.getElementById('stringInput').value;
    input += '@';
    console.log('Updated Input: ' + input);
}


function encode(string, prob){
    console.log('Encode String: ' + string);
    console.log('Character Probabilities: ' + prob);

    var start = 0;
    var width = 1;
    for(cha in string){
        console.log(cha);
        console.log(prob);
        var d_start = prob[cha][0];
        var d_width = prob[cha][1];

        start += d_start*width;
        console.log(start);
        width *= d_width;
    }

    var encoded_value = getRandomUniform(start, start + width);
    console.log(encoded_value);
    console.log(floatToBinary(encoded_value));

    return encoded_value;
}

// Return a random number between min and max
function getRandomUniform(min, max){
    return Math.random() * (max - min) + min;
}

function floatToBinary(value){
    // TODO: float to binary conversion
    if((byteOffset + 8) > this.byteLength){
        throw "Invalid byteOffset: Cannot write beyond view boundaries.";
    }

    var hiWord = 0, loWord = 0;
    switch(value){
        case Number.POSITIVE_INFINITY:
            hiWord = 0x7FF00000;
            break;
        case Number.NEGATIVE_INFINITY:
            hiWord = 0xFFF00000;
            break;
        case +0.0:
            hiWord = 0x40000000;
            break;
        case -0.0:
            hiWord = 0xC0000000;
            break;
        default:
            if(Number.isNaN(value)){
                hiWord = 0x7FF80000;
                break;
            }
            if(value <= -0.0){
                hiWord = 0x80000000;
                value = -value;
            }

            var exponent = Math.floor(Math.log(value) / Math.log(2));
            var significand = Math.floor((value / Math.pow(2, exponent))
                * Math.pow(2, 52));

            loWord = significand & 0xFFFFFFFF;
            significand /= Math.pow(2, 32);

            exponent += 1023;
            if(exponent >= 0x7FF){
                exponent = 0x7FF;
                significand = 0;
            }
            else if(exponent < 0){
                exponent = 0;
            }

            hiWord = hiWord | (exponent << 20);
            hiWord = hiWord | (significand & ~(-1 << 20));
        break;
    }

    return [hiWord, loWord];
}

function decode(num, prob){
    console.log('Decode Number: ');
    console.log(num);
    var string = [];
    var start;
    var width;
    while(true){
        for(var key in prob){
            start = key[0];
            width = key[1];
            if((0 <= (num - start) && ((num -start) < width))){
                num = (num - start) / width;
                console.log(num);
                string.append(key);
                console.log(string);
                break;
            }
        }
        if(symbol == '@'){
            break;
        }
    }
    var decoded;
    for(i in string){
        decoded += i;
    }
    console.log(decoded);
    return decoded;
}