import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuideConverter {
	
	public static final Pattern HEADER = Pattern.compile("^[\t]?([[\\d]+\\.]*) (.*)$");
	
	public static final Pattern PREREQ = Pattern.compile("-[ ]*1\\+? [pP]oint[s]?(,| -) [pP]rerequisite ");
	public static final Pattern SKILL  = Pattern.compile("^\\* ([\\w ]+)[ ]?-[ ]?[~]?([\\d-X]+[+]?)(?: [pP]oint[s]?)?[a-zA-Z +]*(,| -)? (.+)");
	
	public static void main(String[] args) throws IOException {
		
		File   dir   = Paths.get("original").toFile();
		File[] files = dir.listFiles(file -> file.getName().endsWith(".txt"));
		
		if (files != null && files.length != 0) {
			
			for (File file : files) {
				
				String buildName = file.getName().split("\\.")[0];
				
				List<String> lines = Files.readAllLines(Paths.get(String.format("original" + File.separator + "%s.txt",
						buildName))); // TODO declare path
				
				List<String> build  = new ArrayList<>();
				List<String> gear   = new ArrayList<>();
				List<String> skills = new ArrayList<>();
				
				String lastHeader     = "";
				int    tableHeadIndex = 0;
				int    firstInTable   = 0;
				
				for (int i = 0; i < lines.size(); i++) {
					
					String line = lines.get(i);
					
					if (line.contains("This links to the most up to date mockup for reference")) {
						continue;
					}
					if (line.isEmpty()) {
						continue;
					}
					
					Matcher matcher = HEADER.matcher(line);
					
					if (matcher.find()) {
						
						// START PAST HEADER - append info to the end of the last header before starting the next header
						
						if (lastHeader.equalsIgnoreCase("Gear")) {
							
							build.add("</ul>");
							gear.add("</ul>");
							gear.add("");
							
						} else if (lastHeader.contains(" Breakpoints")) {
							
							build.add("</ul>");
							build.add("</td>");
							build.add("</tr>");
							build.add("</table>");
							
						} else if (lastHeader.equalsIgnoreCase("Skill Build")) {
							
							build.add("</ol>");
							build.add("");
							build.add("<div class=\"centered\">");
							build.add(
									"  <a class=\"highslide map\" href=\"@@@IMG_D2:builds/CHANGEME-skill-tree.jpg@@@\" onclick=\"return hs.expand(this, { anchor: 'center' })\">");
							build.add(String.format("    <img alt=\"%s Skills\" src=\"@@@IMG_D2:builds/CHANGEME-skill-tree-small.jpg@@@\" />",
									buildName));
							build.add("  </a>");
							build.add("</div>");
							build.add("");
							build.add("<p>CHANGEME</p>");
							
							skills.add("</ol>");
							skills.add("/\\ CHANGEME \\/");
							skills.add("<table>");
							for (int j = 1; j <= 75; j++) {
								int o = 1;
								if (j == 4 || j == 15 || j == 23 || j == 33 || j == 36 || j == 40 || j == 55 || j == 60 || j == 65) {
									o++;
								}
								if (j == 23 || j == 40 || j == 65) {
									o++;
								}
								
								String note = "";
								if (j == 4 || j == 33 || j == 55) {
									note = "Den";
								} else if (j == 15 || j == 36 || j == 60) {
									note = "Rada";
								} else if (j == 23 || j == 40 || j == 65) {
									note = "Izzy";
								}
								
								for (int p = 0; p < o; p++) {
									skills.add("<tr>");
									skills.add(p == 0 ? String.format("<td>Level %s</td>", j) : "<td></td>");
									skills.add("<td>Skill</td>");
									skills.add(p == 0 ? "<td>5 Vitality</td>" : "<td></td>");
									skills.add(String.format("<td>%s</td>", note));
									skills.add("</tr>");
								}
							}
							skills.add("</table>");
							skills.add("");
							skills.add("<div class=\"centered\">");
							skills.add(
									"  <a class=\"highslide map\" href=\"@@@IMG_D2:builds/CHANGEME-skill-tree.jpg@@@\" onclick=\"return hs.expand(this, { anchor: 'center' })\">");
							skills.add(String.format("    <img alt=\"%s Skills\" src=\"@@@IMG_D2:builds/CHANGEME-skill-tree-small.jpg@@@\" />",
									buildName));
							skills.add("  </a>");
							skills.add("</div>");
							//skills.add("");
						} else if (lastHeader.equalsIgnoreCase("Attributes")) {
							
							skills.add("");
							skills.add(String.format("<h1>Select %s Skill Discussion</h1>", buildName));
							skills.add("");
							skills.add("<h2>CHANGEME</h2>");
							skills.add("");
							skills.add("<p>CHANGEME</p>");
							skills.add("");
							
						}
						
						// START CURRENT HEADER
						
						String headerIndex = matcher.group(1);
						lastHeader = matcher.group(2);
						int headerCount = countChars(headerIndex, '.');
						
						//if (lastHeader.equalsIgnoreCase("Introduction")) {
						
						//} else if (lastHeader.equalsIgnoreCase("Quick Reference")) {
						
						
						
						/*} else */
						if (lastHeader.equalsIgnoreCase("Skill Build")) {
							
							build.add("<h2>Skill Build</h2>");
							build.add("");
							build.add(Reformat.format("<p>The following section shows the core skills for the %build_name%. For a detailed " +
									"breakdown of all core and optional skills as well as the recommended order for " +
									"allocating skill points, refer to the Skills section of this guide.</p>", buildName));
							build.add("");
							build.add("<ol>");
							build.add("<li>1 point to all prerequisites</li>");
							
							skills.add(String.format("<h1>%s Skill Build</h1>", buildName));
							skills.add("");
							skills.add("<ol>");
							
						} else if (lastHeader.equalsIgnoreCase("Attributes")) {
							
							build.add("");
							build.add("<h2>Stat Points and Attributes</h2>");
							build.add("");
							build.add("<table>");
							build.add("<tr>");
							build.add("<td>Strength</td>");
							build.add("<td>Dexterity</td>");
							build.add("<td>Vitality</td>");
							build.add("<td>Energy</td>");
							build.add("</tr>");
							build.add("<tr>");
							build.add("<td>Enough for Gear</td>");
							build.add("<td>Enough for Gear</td>");
							build.add("<td>Everything Else</td>");
							build.add("<td>None</td>");
							build.add("</tr>");
							build.add("</table>");
							
							skills.add("");
							skills.add(String.format("<h1>%s Stats Discussion</h1>", buildName));
							skills.add("");
							skills.add(
									"<p>The following section discusses how many attribute points should be allocated to each of the primary stats</p>");
							
						} else if (lastHeader.equalsIgnoreCase("Gear")) {
							
							build.add("");
							build.add(String.format("<h2>%s Gear</h2>", buildName));
							build.add("");
							build.add(Reformat.format("<p>The following section shows some of the most important items for the " +
									"%build_name%. For a detailed per-slot breakdown of all %build_name% items and " +
									"alternative builds, refer to the Gear section of this guide.</p>", buildName));
							build.add("");
							build.add("<ul>");
							firstInTable = 2;
							
						} else if (lastHeader.contains(" Breakpoints")) {
							
							build.add("");
							build.add(String.format("<h%1$s>%2$s</h%1$s>", headerCount, lastHeader));
							build.add("");
							build.add("<table>");
							build.add("<tr>");
							tableHeadIndex = build.size();
							build.add("</tr>");
							build.add("<tr>");
							firstInTable = 2;
							
						} else if (lastHeader.equalsIgnoreCase("Mercenary")) {
							
							build.add("");
							build.add("<h2>Mercenary</h2>");
							gear.add("<h1>Mercenary</h1>");
							gear.add("");
							
						} else if (lastHeader.equalsIgnoreCase("Mercenary Gear")) {
							
							build.add("");
							build.add("<ul>");
							firstInTable = 2;
							gear.add("<h2>Mercenary Gear</h2>");
							gear.add("");
							
						} else {
							
							if (lastHeader.equalsIgnoreCase("Breakpoints") || lastHeader.equalsIgnoreCase("Mercenary") ||
									lastHeader.equalsIgnoreCase("Quick Reference")) {
								build.add("");
							}
							build.add(String.format("<h%1$s>%2$s</h%1$s>", headerCount, lastHeader));
							if (/*!lastHeader.equalsIgnoreCase("Quick Reference") &&*/ !lastHeader.equalsIgnoreCase("Breakpoints")) {
								build.add("");
							}
							
						}
						
					} else {
						
						if (lastHeader.equalsIgnoreCase("Skill Build")) {
							
							String newLine = Reformat.format(String.format("<li>%s</li>", line), buildName);
							
							if (line.startsWith("* ")) {
								
								if (PREREQ.matcher(line).find()) {
									continue;
								}
								
								Matcher skill = SKILL.matcher(line);
								if (skill.find()) {
									String skillName = skill.group(1);
									String points    = skill.group(2);
									String extra     = skill.group(4);
									newLine = Reformat.formatOnlySkills(String.format("<li>%s points to %s- %s</li>", points, skillName, extra),
											buildName);
								}
								
							}
							
							if (!newLine.contains("optional")) {
								build.add(newLine);
							}
							skills.add(newLine);
							
						} else if (lastHeader.equalsIgnoreCase("Attributes")) {
							
							if (line.equals("Strength") || line.equals("Dexterity") || line.equals("Vitality") || line.equals("Energy")) {
								skills.add("");
								skills.add(String.format("@@@FAQ_Q3:How much %s Does a %s Need?@@@", line, buildName));
								skills.add("");
								skills.add("@@@FAQ_A_START@@@");
							} else {
								skills.add(Reformat.format(String.format("<p>%s - CHANGEME</p>", line), buildName));
								skills.add("@@@FAQ_A_END@@@");
							}
							
						} else if (lastHeader.equalsIgnoreCase("Gear")) {
							
							if (line.startsWith("* ")) {
								
								line = line.substring(2).trim();
								String gearLine = Reformat.formatOnlyItems(String.format("<li>%s</li>", line), buildName);
								if (firstInTable == 1 &&
										(line.startsWith("Weapon:") || line.startsWith("Shield:") || line.startsWith("Body Armor:") ||
												line.startsWith("Helm:") || line.startsWith("Belt:"))) {
									build.add(gearLine);
								}
								gear.add(gearLine);
								
							} else if (line.startsWith("Note: ")) {
								
								gear.add(Reformat.formatOnlyItems(String.format("<li>%s</li>", line), buildName));
								
							} else {
								
								if (firstInTable == 2) {
									firstInTable = 1;
								} else if (firstInTable == 1) {
									firstInTable = 0;
									gear.add("</ul>");
									gear.add("");
								} else {
									gear.add("</ul>");
									gear.add("");
								}
								
								gear.add(String.format("<h1>%s</h1>", line));
								gear.add("");
								gear.add("<p>CHANGEME</p>");
								gear.add("");
								gear.add("<ul>");
								
							}
							
						} else if (lastHeader.contains(" Breakpoints")) {
							
							line = line.replace("\t", "").trim();
							
							if (line.equals("...")) {
								continue;
							}
							
							if (line.equals("Frames to perform action") || line.equals("Stat and amount required to reach frame")) {
								
								if (firstInTable == 2) {
									firstInTable = 0;
								} else {
									build.add("</ul>");
									build.add("</td>");
								}
								
								build.add(tableHeadIndex++, String.format("<td><center><h3>%s</h3></center></td>", line));
								build.add("<td>");
								build.add("<ul>");
								
							} else {
								
								build.add(Reformat.formatOnlyItems(String.format("<li>%s</li>", line), buildName));
								
							}
							
						} else if (lastHeader.equalsIgnoreCase("Mercenary")) {
							
							String mercLine = Reformat.format(String.format("<p>%s</p>", line.trim()), buildName);
							
							build.add("");
							build.add(mercLine);
							gear.add(mercLine);
							// TODO maybe needs to change loc
							
						} else if (lastHeader.equalsIgnoreCase("Mercenary Gear")) {
							
							if (line.startsWith("* ")) {
								
								line = line.substring(2).trim();
								String gearLine = Reformat.formatOnlyItems(String.format("<li>%s</li>", line), buildName);
								if (firstInTable == 1) {
									build.add(gearLine);
								}
								gear.add(gearLine);
								
							} else {
								
								if (firstInTable == 2) {
									firstInTable = 1;
								} else if (firstInTable == 1) {
									firstInTable = 0;
									gear.add("</ul>");
									gear.add("");
								} else {
									gear.add("</ul>");
									gear.add("");
								}
								
								gear.add(String.format("<h3>%s</h3>", line));
								gear.add("");
								gear.add("<p>CHANGEME</p>");
								gear.add("");
								gear.add("<ul>");
								
							}
							
						} else {
							
							build.add(Reformat.format(String.format("<p>%s</p>", line.trim()), buildName));
							
						}
						
					}
					
					if (i == lines.size() - 1) {
						
						if (lastHeader.equalsIgnoreCase("Mercenary Gear")) {
							
							build.add("</ul>");
							build.add("");
							/*build.add(String.format(
									"<p>For a detailed per-slot breakdown of %s Mercenary gear, refer to the Gear section of this guide.</p>",
									buildName));
							build.add("");*/
							build.add("<h2>Early Leveling</h2>");
							build.add("");
							build.add("<p>CHANGEME</p>");
							build.add("");
							
							gear.add("</ul>");
							gear.add("");
							gear.add("<h1>Build Discussion</h1>");
							gear.add("");
							gear.add("<p>CHANGEME</p>");
							gear.add("");
							
						} else if (lastHeader.contains(" Breakpoints")) {
							build.add("</ul>");
							build.add("</td>");
							build.add("</tr>");
							build.add("</table>");
						}
						
					}
					
				}
				
				File guideDir = new File("converted" + File.separator + buildName.toLowerCase());
				if (guideDir.exists() || guideDir.mkdirs()) {
					Files.write(Paths.get("converted" + File.separator + buildName.toLowerCase() + File.separator + "build.content"), build);
					Files.write(Paths.get("converted" + File.separator + buildName.toLowerCase() + File.separator + "gear.content"), gear);
					Files.write(Paths.get("converted" + File.separator + buildName.toLowerCase() + File.separator + "skills.content"), skills);
				}
				
			}
			
		}
		
	}
	
	public static int countChars(String string, char c) {
		int count = 0;
		for (int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == c) {
				count++;
			}
		}
		return count;
	}
	
}
