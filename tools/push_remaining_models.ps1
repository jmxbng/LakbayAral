$ErrorActionPreference = "Continue"
Set-Location "C:\LakbayAralClaude\LakbayAral"

$log = Join-Path $PSScriptRoot "push_remaining_models.log"
function Log([string]$msg) {
    $line = "{0}  {1}" -f (Get-Date -Format "HH:mm:ss"), $msg
    Add-Content -Path $log -Value $line
    Write-Host $line
}

$env:GIT_HTTP_LOW_SPEED_LIMIT = "1000"
$env:GIT_HTTP_LOW_SPEED_TIME = "600"
git config http.postBuffer 524288000
git config http.version HTTP/1.1

$root = (Resolve-Path .).Path
$files = Get-ChildItem "app\src\main\assets\models" -Recurse -Filter "*.glb" | Sort-Object FullName
Log ("Found {0} glb files on disk" -f $files.Count)

$pushed = 0
$skipped = 0
$failed = 0

foreach ($f in $files) {
    $rel = $f.FullName.Substring($root.Length + 1).Replace('\', '/')
    git ls-files --error-unmatch -- $rel 2>$null | Out-Null
    if ($LASTEXITCODE -eq 0) {
        $skipped++
        continue
    }

    $sizeMb = [math]::Round($f.Length / 1MB, 1)
    Log ("COMMIT {0} ({1} MB)" -f $rel, $sizeMb)
    git add -- $rel
    git commit -m "Add 3D model $rel"
    if ($LASTEXITCODE -ne 0) {
        Log ("COMMIT FAILED $rel")
        $failed++
        exit 1
    }

    $ok = $false
    for ($r = 1; $r -le 10; $r++) {
        git push origin main
        if ($LASTEXITCODE -eq 0) {
            $ok = $true
            $pushed++
            Log ("PUSHED OK $rel  (this session: $pushed, skipped already on remote: $skipped)")
            break
        }
        Log ("PUSH RETRY $r for $rel")
        Start-Sleep -Seconds ([Math]::Min(60, 8 * $r))
    }

    if (-not $ok) {
        Log ("FAILED PERMANENTLY $rel")
        $failed++
        exit 1
    }
}

Log ("DONE pushed=$pushed skipped=$skipped failed=$failed")
git status -sb | Out-File -FilePath (Join-Path $PSScriptRoot "push_remaining_models.status.txt")
exit 0
