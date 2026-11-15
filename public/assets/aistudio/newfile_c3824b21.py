import os
import time
import threading

#tiny update Shop$

lv7 = 0
gia7 = 3
lv6 = 0
gia6 = 3
lv5 = 0
thay2 = 0.1
giamgia = 1
gia5 = 5
thay = 0.125
lv4 = 0
gia4 = 75
cd4 = 1
cd3 = 1
cd2 = 1 #countdown dung cho auto click
cd = 0.25
click = 0
lv3 = 0
lv = 0
lv2 = 0
gia2 = 50
gia3 = 150
muti = 1.0
muti2 = 1.0
gia = 25
x2 = 1 #so diem nhan duoc dung cho auto click
x = 1
diem = 0
do = 0
        	
def stats():
    	print("STATS")
    	print("diem hien tai: ", round(diem))
    	print("$", do)
    	print("he so nhan: ", muti)
    	print(round(x * muti, 2), "diem / click")
    	print("ban da click duoc ", click, "lan")
    	print("countdown / click :", cd,"s")

gioihan = True     
auto_enabled = False

def auto_worker():
    global diem, click

    while True:
        if auto_enabled:
            diem = diem + (x2 * muti)
            click = click + 1
            time.sleep(cd2)
        else:
        	time.sleep(0.1)

threading.Thread(target=auto_worker, daemon=True).start()
            		   	
while True:
    os.system("clear")
    stats()
    print("")
    print("")
    print("")
    print("1: shop")
    print("2: restarts")
    print("3: $shop$")
    print("0: thoat game")

    A = input("nhan enter de bat dau: ")
    
    if A not in ["0", "1", "2", "3",""]:
    	print("lua chon khong hop le")
    	time.sleep(cd4)

    if A == "0":
        os.system("clear")
        print("cam on vi da choi")
        break

    if A == "":
        os.system("clear")
        diem = diem + (x * muti)
        click = click + 1
        print("click!")
        time.sleep(cd)

    if A == "3":
        while True:
            os.system("clear")
            stats()
            print("")
            print("")
            print("")
            print("1: doi diem sang tien$ (1000 diem/$1)")
            print("2: doi toan bo diem sang tien$ (1000 diem/$1)")
            print("3: vao $shop$")
            print("0: thoat khoi phan doi diem")

            C = input("chon: ")
            
            if C not in ["0", "1", "2", "3"]:
            	print("lua chon khong hop le")
            	time.sleep(cd4)

            if C == "1":
                if diem >= 1000:
                    print("doi diem sang tien$ thanh cong")
                    diem = diem - 1000
                    do = do + 1
                    time.sleep(cd4)
                else:
                    print("ban chua du diem de doi sang tien$")
                    print("so diem con lai can de doi sang tien$ :", round(1000 - diem),"diem")
                    time.sleep(cd4)

            if C == "2":
                if diem >= 0:
                    print("doi diem sang tien$ thanh cong")
                    print("ban da doi duoc tu", do,"$ thanh ", do + (diem / 1000))
                    do = do + (diem / 1000)
                    diem = diem - diem
                    time.sleep(cd4)
                    
            if C == "3":
            	while True:
            		os.system("clear")
            		stats()
            		print("")
            		print("")
            		print("")
            		print("1: giam gia /",gia5,"$ / lv",lv5)
            		print("2: giam thoi gian roi khoi menu / ", gia6,"$ / lv",lv6)
            		print("3: giam thoi gian thong bao /", gia7,"$ / lv",lv7)
            		print("4: auto click / 25$")
            		print("5: nang cap auto click")
            		print("0: thoat khoi $shop$")
            		
            		E = input("chon: ")
            		
            		if E not in ["1","2","3","4","5","0"]:
            			print("lua chon khong hop le")
            			time.sleep(cd4)

            		
            		if E == "5":
            			while True:
            				os.system("clear")
            				stats()
            				print("")
            				print("")
            				print("")
            				print("coming soon...")
            				print("0: thoat khoi menu nang cap auto click")
            				
            				F = input("chon: ")
            				
            				if F not in ["0"]:
            					print("lua chon khong hop le")
            					time.sleep(cd4)

            				
            				if F == "0":
            					print("dang thoat khoi Menu nang cap auto click")
            					time.sleep(cd3)
            					break
            					
            		
            		if E == "3":
            			if do >= gia7:
            				do = do - gia7
            				cd4 = cd4 - (cd4 / 2)
            				gia7 = gia7 * 3
            				lv7 = lv7 + 1
            				print("mua vat pham thanh cong")
            				time.sleep(cd4)
            			else:
            				print("ban khong co du tien$ de mua vat pham")
            				print("so tien$ con lai can de mua vat pham :", round(gia7 - do),"$")
            				time.sleep(cd4)
            				
            		if E == "2":
            			if do >= gia6:
            				do = do - gia6
            				cd3 = cd3 - (cd3 / 2)
            				gia6 = gia6 * 3
            				lv6 = lv6 + 1
            				print("mua vat pham thanh cong")
            				time.sleep(cd4)
            			else:
            				print("ban khong co du tien de mua vat pham")
            				print("so tien$ con lai can de mua vat pham :", round(gia6 - do),"$")
            				time.sleep(cd4)
            		
            		if E == "4":
            			if do >= 25:
            				if gioihan:
            					do = do - 25
            					gioihan = False
            					auto_enabled = True
            					print("mua vat pham thanh cong")
            					time.sleep(cd4)
            				else:
            					print("ban da mua nang cap nay roi")
            					time.sleep(cd4)
            			else:
            				print("ban khong co du tien de mua vat pham")
            				print("so tien$ con lai can de mua vat pham :", round(25 - do),"$")
            				time.sleep(cd4)
            		
            		if E == "1":
            			if do >= gia5:
            					do = do - gia5
            					gia5 = gia5 * 3
            					giamgia = giamgia + thay2
            					thay2 = thay2 * 2
            					lv5 = lv5 + 1
            					print("mua vat pham thanh cong")
            					time.sleep(cd4)
            			else:
            					print("ban khong du tien de mua vat pham")
            					print("so tien$ con lai can de mua vat pham :", round(gia5 - do),"$")
            					time.sleep(cd4)
            					
            		if E == "0":
            			print("dang thoat khoi $shop$")
            			time.sleep(cd3)
            			break 

            if C == "0":
                print("dang thoat khoi phan doi diem")
                time.sleep(cd3)
                break

    if A == "2":
        while True:
            os.system("clear")
            stats()
            print("")
            print("")
            print("")
            print("lv restarts:", lv3)
            print("restarts tiep theo:")
            print("he so nhan ban dau: ", muti2, "->", muti2 + 0.5)
            print("can", round(gia3 / giamgia), "diem de restarts")
            print("")
            print("1: restarts")
            print("0: thoat khoi restart")

            B = input("chon: ")
            
            if B not in ["0", "1"]:
            	print("lua chon khong hop le")
            	time.sleep(cd4)

            if B == "1":
            	if diem >= round(gia3 / giamgia):
            		print("restarts thanh cong")
            		auto_enabled = False
            		gia3 = gia3 * 3
            		lv3 = lv3 + 1
            		muti = muti2 + 0.5
            		muti2 = muti
            		diem = 0
            		do = 0
            		click = 0
            		lv = 0
            		lv4 = 0
            		gia = 25
            		gia4 = 75
            		lv2 = 0
            		gia2 = 50
            		x = 1
            		thay = 0.1
            		cd = 0.25
            		giamgia = 1
            		thay2 = 0.1
            		lv5 = 0
            		lv6 = 0
            		gia5 = 5
            		gia6 = 3
            		cd3 = 1
            		gioihan = True
            		x2 = 1
            		cd2 = 1
            		cd4 = 1
            		gia7 = 3
            		lv7 = 0
            		time.sleep(cd4)
            	else:
            		print("ban chua du diem de restarts")
            		print("so diem con lai can co de restarts :", round(round(gia3/giamgia) - diem),"diem")
            		time.sleep(cd4)

            if B == "0":
                print("dang thoat khoi restarts")
                time.sleep(cd3)
                break

    if A == "1":
        while True:
            os.system("clear")
            stats()
            print("")
            print("")
            print("")
            print("1: +", x, "them diem nhan duoc /",round(gia / giamgia), "diem / lv ", lv)
            print("2: them he so nhan /", round(gia2 / giamgia), "diem / lv ", lv2)
            print("3: giam countdown giua cac lan click /", round(gia4 / giamgia ), "diem / lv ", lv4)
            print("0: thoat")

            D = input("chon: ")
            
            if D not in ["0", "1", "2", "3"]:
            	print("lua chon khong hop le")
            	time.sleep(cd4)

            if D == "3":
                if diem >= round(gia4 / giamgia):
                    print("mua nang cap thanh cong")
                    diem = diem - (gia4 / giamgia)
                    gia4 = gia4 * 3
                    lv4 = lv4 + 1
                    cd = cd - thay
                    thay = thay / 2
                    time.sleep(cd4)
                else:
                    print("ban chua du diem de mua nang cap")
                    print("so diem con lai can de mua nang cap :", round(round(gia4/giamgia) - diem),"diem")
                    time.sleep(cd4)

            if D == "2":
                if diem >= round(gia2 / giamgia):
                    print("mua nang cap thanh cong")
                    diem = diem - (gia2 / giamgia)
                    muti = muti + 0.1
                    gia2 = gia2 * 3
                    lv2 = lv2 + 1
                    time.sleep(cd4)
                else:
                    print("ban chua du diem de mua nang cap")
                    print("so diem con lai can de mua nang cap :", round(round(gia2/giamgia) - diem),"diem")
                    time.sleep(cd4)

            if D == "1":
                if diem >= round(gia / giamgia):
                    print("mua nang cap thanh cong")
                    diem = diem - (gia / giamgia)
                    x = x * 2
                    gia = gia * 3
                    lv = lv + 1
                    time.sleep(cd4)
                else:
                    print("ban chua du diem de mua nang cap")
                    print("so diem con lai can de mua nang cap :", round(round(gia/giamgia) - diem),"diem")
                    time.sleep(cd4)

            if D == "0":
                print("dang thoat khoi shop")
                time.sleep(cd3)
                break