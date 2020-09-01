package control;

import model.User;

public class TitleCheck{

	public int checkRole(User u) {
		int kenri = 0;
		if("teacher".equals(u.getRole())) {
			kenri = 1;
		}
		if("manager".equals(u.getRole())) {
			kenri = 2;
		}
		return kenri;
	}
	public int checkPass(String pass) {
		int err = 0;
		int len = pass.length();
		if(len < 8 || len > 32) {
			err = 1;
		}else{
			err = 2;
			for(int i = 0;i < pass.length();i++) {
				if(Character.isUpperCase(pass.charAt(i))) {
					err = 0;
					break;
				}
			}
			if(err == 2) {
				err = 4;
				for(int i = 0;i < pass.length();i++) {
					if(Character.isDigit(pass.charAt(i))) {
						err = 2;
						break;
					}
				}

			}else {
				err = 3;
				for(int i = 0;i < pass.length();i++) {
					if(Character.isDigit(pass.charAt(i))) {
						err = 0;
						break;
					}
				}
			}
		}
		return err;
	}
}