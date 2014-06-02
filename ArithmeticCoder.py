import random
import sys
import struct

# Character probablities
prob = {
    'a' : (0.0, 0.3),
    'b' : (0.3, 0.2),
    'c' : (0.5, 0.2),
    'd' : (0.7, 0.2),
    '@' : (0.9, 0.1)
}

def encode(string, prob):
    print('Encode String: ' + string)
    print('Character Probabilities: ')
    print(prob)
    start = 0
    width = 1
    for ch in string:
        d_start, d_width = prob[ch]
        start += d_start*width
        print(start)
        width *= d_width
        print(width)

    encoded_value = random.uniform(start, start+width)
    print(encoded_value)
    print(floatToBinary(encoded_value))
    return encoded_value

def floatToBinary(value):
    val = struct.unpack('Q', struct.pack('d', value))[0]
    return bin(val)

def binaryToFloat(value):
    hx = hex(int(str(value), 2))
    return struct.unpack('d', struct.pack('q', int(hx, 16)))[0]

def decode(num, prob):
    print('Decode Number: ')
    print(num)
    string = []
    while True:
        for symbol, (start, width) in prob.iteritems():
            if 0 <= num - start < width:
                num = (num - start) / width
                print(num)
                string.append(symbol)
                print(string)
                break
        if symbol == '@':
            break

    decoded = ''.join(string)
    print("Decoded String: " + decoded)
    return decoded

def main(number_to_encode):
    encoded_number = encode(number_to_encode, prob)
    decoded_number = decode(encoded_number, prob)

if __name__ == '__main__':
    main(sys.argv[1])