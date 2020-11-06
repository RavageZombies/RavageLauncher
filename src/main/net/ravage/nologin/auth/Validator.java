/*
 * Copyright 2015 Lifok
 *
 * This file is part of NoLogin.

 * NoLogin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NoLogin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NoLogin.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.ravage.nologin.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ravage.login.AuthPoints;
import net.ravage.login.AuthenticationException;
import net.ravage.login.Authenticator;
import net.ravage.login.model.response.RefreshResponse;
import net.ravage.nologin.account.Account;
import net.ravage.nologin.util.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Validator {

	private String clientToken;
	private Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);

	public Validator() 
	{
		super();
	}

	/**
	 * Call this method to validate the AccessToken of an account, it will automatically try to refresh if validation fail.
	 * @param acc
	 * @return true is the validation or the refresh success. 
	 */
	public boolean validateAccount(Account acc) 
	{
		try 
		{
			authenticator.validate(acc.getAccessToken());
			return true;
		} 
		catch (AuthenticationException e)
		{
			return refreshToken(acc);
		}
	}

	private boolean refreshToken(Account acc)
	{
		try 
		{
			RefreshResponse response = authenticator.refresh(acc.getAccessToken(), getClientToken());
			acc.setAccessToken(response.getAccessToken());
			updateMcFile(acc, response);
			return true;
		}
		catch (AuthenticationException e) 
		{
			System.out.println(e.getErrorModel().getErrorMessage());
			return false;
		}
	}

	/**
	 * Used to rewrite the launcher_profiles file
	 * @return
	 */
	private void updateMcFile(Account acc, RefreshResponse response)
	{
		File profiles = new File(Utilities.getMinecraftDirectory(), "launcher_profiles.json");
		try 
		{
			FileInputStream fis = new FileInputStream(profiles);
			byte[] data = new byte[(int) fis.available()];
			fis.read(data);
			fis.close();
			String jsonProfiles = new String(data, "UTF-8");
			JsonObject profilesObj = (JsonObject) (new JsonParser()).parse(jsonProfiles);
			JsonObject profileObj = profilesObj.getAsJsonObject("authenticationDatabase").getAsJsonObject(response.getSelectedProfile().getId());
			profileObj.remove("accessToken");
			profileObj.addProperty("accessToken", response.getAccessToken());
			FileWriter writer = new FileWriter(profiles);
			writer.write(profilesObj.toString());
			writer.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Used to retrieve the ClientToken from the profiles file
	 * @return
	 */
	public String getClientToken() 
	{
		if(clientToken == null) 
		{
			File profiles = new File(Utilities.getMinecraftDirectory(), "launcher_profiles.json");
			try 
			{
				FileInputStream fis = new FileInputStream(profiles);
				byte[] data = new byte[(int) fis.available()];
				fis.read(data);
				fis.close();
				String jsonProfiles = new String(data, "UTF-8");
				JsonObject profilesObj = (JsonObject) (new JsonParser()).parse(jsonProfiles);
				clientToken = profilesObj.get("clientToken").getAsString();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return clientToken;
	}
}
