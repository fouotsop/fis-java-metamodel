from flask import Blueprint, jsonify, request

from app.reputation.domain_authority_sensor import DomainRatingSensor
from app.security.ssl_certificate_health_sensor import SSLCertificateHealthSensor
from app.security.security_header_sensor import SecurityHeaderSensor
from app.transparency.contact_information_sensor import ContactInformationSensor
from app.transparency.policies_sensor import PolicySensor
from app.reputation.social_media_presence_sensor import SocialMediaPresenceSensor
from app.reputation.domain_age_sensor import DomainAgeSensor
from app.transparency.tosdr_policy_sensor import TOSDRPolicySensor

from flask import current_app
# Import the nlp model if needed for analysis
# from . import nlp 


main_bp = Blueprint('main', __name__)


@main_bp.route('/sense-security-headers', methods=['GET'])
def sense_security_headers():
    url = request.args.get('url')
    url = "https://" + url if url and not url.startswith(('http://', 'https://')) else url
    if not url:
        return jsonify({"error": "URL is required"}), 400

    security_header_sensor: SecurityHeaderSensor = SecurityHeaderSensor()
    try:
        score = security_header_sensor.sense(url)
    except RuntimeError as exc:
        return jsonify({"url": url, "error": str(exc)}), 502

    return jsonify({"url": url, "security_score": score}), 200

@main_bp.route('/sense-ssl-certificate-health', methods=['GET'])
def sense_ssl_certificate_health():
    url = request.args.get('url')
    url = "https://" + url if url and not url.startswith(('http://', 'https://')) else url
    if not url:
        return jsonify({"error": "URL is required"}), 400

    ssl_sensor = SSLCertificateHealthSensor(url)
    ssl_scores = ssl_sensor.sense()
    return jsonify({"url": url, "ssl_scores": ssl_scores}), 200


@main_bp.route('/sense-policies', methods=['GET'])
def sense_policies():
    url = request.args.get('url')
    url = "https://" + url if url and not url.startswith(('http://', 'https://')) else url
    if not url:
        return jsonify({"error": "URL is required"}), 400
    
    policy_sensor = PolicySensor()
    try:
        policies = policy_sensor.sense(url)
    except Exception as exc:
        return jsonify({"error": f"Failed to analyze policies: {str(exc)}"}), 500

    return jsonify({"policies": policies}), 200


@main_bp.route('/sense-social-media-presence', methods=['GET'])
def sense_social_media_presence():
    url = request.args.get('url')

    if url.startswith(('http://', 'https://')):
        url = url.split("://")[1].split("/")[0]  # Extract domain from URL

    if not url:
        return jsonify({"error": "URL is required"}), 400

    social_sensor = SocialMediaPresenceSensor()
    try:
        presence = social_sensor.get_social_presence(url)
    except Exception as exc:
        return jsonify({"error": f"Failed to analyze social media presence: {str(exc)}"}), 500

    return jsonify({"social_media_presence": presence}), 200


@main_bp.route('/sense-domain-age', methods=['GET'])
def sense_domain_age():
    domain = request.args.get('domain')
    if not domain:
        return jsonify({"error": "Domain is required"}), 400

    age_sensor = DomainAgeSensor()
    try:
        age_info = age_sensor.sense(domain)
    except Exception as exc:
        return jsonify({"error": f"Failed to analyze domain age: {str(exc)}"}), 500

    return jsonify({"domain_age_info": age_info}), 200


@main_bp.route('/sense-contact-information', methods=['GET'])
def sense_contact_information():
    domain = request.args.get('domain')
    if not domain:
        return jsonify({"error": "Domain is required"}), 400

    contact_sensor = ContactInformationSensor()
    try:
        contact_info = contact_sensor.sense(domain)
    except Exception as exc:
        return jsonify({"error": f"Failed to analyze contact information: {str(exc)}"}), 500

    return jsonify({"contact_information": contact_info}), 200


@main_bp.route('/sense-ux-design', methods=['GET'])
def sense_ux_design():
    url = request.args.get('url')
    url = "https://" + url if url and not url.startswith(('http://', 'https://')) else url
    if not url:
        return jsonify({"error": "URL is required"}), 400

    from app.ux_design.ux_design_sensor import UXDesignSensor
    ux_sensor = UXDesignSensor()
    try:
        ux_scores = ux_sensor.fetch_data(url)
    except Exception as exc:
        return jsonify({"error": f"Failed to analyze UX design: {str(exc)}"}), 500

    return jsonify({"url": url, "ux_scores": ux_scores}), 200


@main_bp.route('/sense-tosdr-policies', methods=['GET'])
def sense_tosdr_policies():
    service_name = request.args.get('service_name')

    if not service_name:
        return jsonify({"error": "Service name is required"}), 400
    
    if service_name.startswith(('http://', 'https://')):
        service_name = service_name.split("://")[1].split("/")[0]  # Extract domain from URL
    
    if service_name.startswith('www.'):
        service_name = service_name[4:]  # Remove 'www.' prefix if present

    tosdr_sensor = TOSDRPolicySensor(tosdr_url=None)  # Use default URL from config
    try:
        result = tosdr_sensor.sense_service_policy(service_name)
    except Exception as exc:
        return jsonify({"error": f"Failed to search TOSDR policies: {str(exc)}"}), 500

    return jsonify({"service_name": service_name, "policies": result}), 200

@main_bp.route('/sense-domain-authority', methods=['GET'])
def sense_domain_authority():
    domain = request.args.get('domain')
    if not domain:
        return jsonify({"error": "Domain is required"}), 400
        
    api_url = current_app.config.get('DOMAIN_AUTHORITY_API_URL')

    authority_sensor = DomainRatingSensor(api_url=api_url)
    try:
        authority_info = authority_sensor.get_domain_rating(domain)
    except Exception as exc:
        return jsonify({"error": f"Failed to analyze domain authority: {str(exc)}"}), 500

    return jsonify({"domain_authority_info": authority_info}), 200